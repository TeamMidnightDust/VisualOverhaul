package eu.midnightdust.visualoverhaul;

import com.mojang.blaze3d.systems.RenderSystem;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import eu.midnightdust.visualoverhaul.mixin.TextureManagerAccessor;
import eu.midnightdust.visualoverhaul.util.ModIconUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;

import java.io.InputStream;
import java.util.Properties;

public class IconicButtons {
    MinecraftClient client = MinecraftClient.getInstance();
    private String buttonId;
    private Text prevText;
    private Identifier iconId;
    public IconicButtons(ClickableWidget widget) {
        init(widget);
    }
    public void init(ClickableWidget widget) {
        prevText = widget.getMessage();
        buttonId = (widget.getMessage().getContent() instanceof TranslatableTextContent translatableTextContent) ? translatableTextContent.getKey().toLowerCase() : "";
        if (VOConfig.buttonIcons && !buttonId.equals("")) {
            if (VOConfig.debug) System.out.println(buttonId);
            iconId = Identifier.tryParse("iconic:textures/gui/icons/" + buttonId.toLowerCase()+".png");
            if (buttonId.endsWith(".midnightconfig.title"))
            {
                iconId = new Identifier("modid", buttonId.replace(".midnightconfig.title", "") + "_icon");
                NativeImageBackedTexture icon = new ModIconUtil(buttonId.replace(".midnightconfig.title", "")).createModIcon();
                if (icon != null) {
                    client.getTextureManager().registerTexture(iconId, icon);
                } else {
                    iconId = null;
                }
            }
            if (iconId == null) return;
            TextureManager textureManager = client.getTextureManager();
            AbstractTexture abstractTexture = textureManager.getOrDefault(iconId, null);
            if (abstractTexture == null) {
                abstractTexture = new ResourceTexture(iconId);
                try { abstractTexture.load(((TextureManagerAccessor)textureManager).getResourceContainer());
                } catch (Exception e) {iconId = null; return;}
                textureManager.registerTexture(iconId, abstractTexture);
            }
            if (abstractTexture == MissingSprite.getMissingSpriteTexture()) iconId = null;
        }
    }
    public void renderIcons(DrawContext context, ClickableWidget widget, float alpha) {
        if (widget.getMessage() == null) return;
        if (prevText != widget.getMessage()) init(widget);
        if (VOConfig.buttonIcons && !buttonId.equals("") && iconId != null) {
            int scaledWidth = client.getWindow().getScaledWidth();

            boolean limitedSpace = client.textRenderer.getWidth(widget.getMessage()) > (widget.getWidth() * 0.75f);
            boolean showLeftWhenBoth = (VOConfig.buttonIconPosition.equals(VOConfig.IconPosition.BOTH) && !limitedSpace) || (VOConfig.buttonIconPosition.equals(VOConfig.IconPosition.BOTH) && widget.getX() < scaledWidth/2);
            boolean showRightWhenBoth = (VOConfig.buttonIconPosition.equals(VOConfig.IconPosition.BOTH) && !limitedSpace) || (VOConfig.buttonIconPosition.equals(VOConfig.IconPosition.BOTH) && widget.getX() > scaledWidth/2);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            if (!widget.active) RenderSystem.setShaderColor(0.3F, 0.3F, 0.3F, alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            int inset = 2;
            if (VOConfig.zoomIconOnHover && widget.isSelected()) inset = 1;
            int size = 20-inset*2;

            if (VOConfig.buttonIconPosition.equals(VOConfig.IconPosition.LEFT) || showLeftWhenBoth || (VOConfig.buttonIconPosition.equals(VOConfig.IconPosition.LOCATION) && widget.getX() < scaledWidth/2))
                context.drawTexture(iconId, widget.getX()+inset, widget.getY()+inset, 0, 0, size, size, size, size);

            if (VOConfig.buttonIconPosition.equals(VOConfig.IconPosition.RIGHT) || showRightWhenBoth || (VOConfig.buttonIconPosition.equals(VOConfig.IconPosition.LOCATION) && widget.getX()+widget.getWidth() > scaledWidth/2))
                context.drawTexture(iconId, widget.getX()+widget.getWidth()-20+inset, widget.getY()+inset, 0, 0, size, size, size, size);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
    public static void reload(ResourceManager manager) {
        manager.findResources("textures", path -> path.getPath().contains(".properties")).forEach((id, resource) -> {
            if (manager.getResource(id).isEmpty()) return;
            try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                Identifier iconId = new Identifier(id.getNamespace(), id.getPath().replace(".properties", ".png"));
                if (manager.getResource(iconId).isPresent()) return;

                Properties properties = new Properties();
                properties.load(stream);
                while (properties.get("properties") != null) {
                    Identifier propertiesId = new Identifier(properties.getProperty("properties"));
                    String textureId = propertiesId.toString().replace(".properties", ".png");

                    properties.clear();
                    if (manager.getResource(Identifier.tryParse(textureId)).isPresent()) { // If a texture is present at the location of the referenced properties file, use that instead
                        properties.put("texture", textureId);
                    }
                    else if (manager.getResource(propertiesId).isPresent()) {
                        properties.load(manager.getResource(propertiesId).get().getInputStream()); // Else load the referenced properties file, if present
                    } else return;
                }

                if (properties.get("texture") != null) {
                    Identifier textureId = new Identifier(properties.getProperty("texture"));
                    TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
                    AbstractTexture abstractTexture = textureManager.getOrDefault(iconId, null);
                    if (abstractTexture == null) {
                        abstractTexture = new ResourceTexture(textureId);
                        textureManager.registerTexture(iconId, abstractTexture);
                    }
                }
            } catch (Exception e) {
                LogManager.getLogger("Iconic").error("Error occurred while loading texture.properties " + id.toString(), e);
            }
        });
    }
}
