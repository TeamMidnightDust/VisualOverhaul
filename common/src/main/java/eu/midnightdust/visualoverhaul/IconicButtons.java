package eu.midnightdust.visualoverhaul;

import com.mojang.blaze3d.systems.RenderSystem;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import eu.midnightdust.visualoverhaul.mixin.TextureManagerAccessor;
import eu.midnightdust.visualoverhaul.util.ModIconUtil;
import eu.midnightdust.visualoverhaul.util.VOColorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import static eu.midnightdust.visualoverhaul.util.VOColorUtil.alphaAndBrightness;

public class IconicButtons {
    MinecraftClient client = MinecraftClient.getInstance();
    TextureManager textureManager = client.getTextureManager();
    private String buttonId;
    private Text prevText;
    private Identifier iconId;
    private static final Map<Identifier, Identifier> ICONS = new HashMap<>();

    public IconicButtons() {}
    public void init(ClickableWidget widget) {
        if (widget == null || widget.getMessage() == null || widget.getMessage().getContent() == null) return;
        prevText = widget.getMessage();
        buttonId = (widget.getMessage().getContent() instanceof TranslatableTextContent translatableTextContent) ? translatableTextContent.getKey().toLowerCase() : "";
        if (VOConfig.buttonIcons && !buttonId.isEmpty()) {
            if (VOConfig.debug) System.out.println(buttonId);
            iconId = Identifier.tryParse("iconic:textures/gui/icons/" + buttonId.toLowerCase()+".png");
            // Show mod icons in MidnightConfig overview
            if (buttonId.endsWith(".midnightconfig.title")) {
                iconId = Identifier.of("modid", buttonId.replace(".midnightconfig.title", "") + "_icon");
                NativeImageBackedTexture icon = new ModIconUtil(buttonId.replace(".midnightconfig.title", "")).createModIcon();
                if (icon != null) {
                    client.getTextureManager().registerTexture(iconId, icon);
                    ICONS.put(iconId, iconId);
                } else {
                    iconId = null;
                }
            }
            // Handle dynamic icons
            else if (iconId != null && !ICONS.containsKey(iconId)) {
                if (((TextureManagerAccessor)textureManager).getResourceContainer().getResource(iconId).isEmpty())
                    return; // If no icon is present, don't load it

                ResourceTexture abstractTexture = new ResourceTexture(iconId);
                if (VOConfig.debug) System.out.println("Loading dynamic icon: "+iconId);
                try {
                    abstractTexture.loadContents(((TextureManagerAccessor)textureManager).getResourceContainer());
                } catch (Exception e) {e.fillInStackTrace();}
                textureManager.registerTexture(iconId, abstractTexture);
                ICONS.put(iconId, iconId);
            }
        }
    }
    public void renderIcons(DrawContext context, ClickableWidget widget, float alpha) {
        if (widget.getMessage() == null || widget.getWidth() <= 20) return;
        if (prevText != widget.getMessage()) init(widget);
        if (VOConfig.buttonIcons && !buttonId.isEmpty() && iconId != null && ICONS.containsKey(iconId)) {
            int scaledWidth = client.getWindow().getScaledWidth();

            boolean limitedSpace = client.textRenderer.getWidth(widget.getMessage()) > (widget.getWidth() * 0.75f);
            boolean showLeftWhenBoth = (VOConfig.buttonIconPosition.equals(VOConfig.IconPosition.BOTH) && !limitedSpace) || (VOConfig.buttonIconPosition.equals(VOConfig.IconPosition.BOTH) && widget.getX() < scaledWidth/2);
            boolean showRightWhenBoth = (VOConfig.buttonIconPosition.equals(VOConfig.IconPosition.BOTH) && !limitedSpace) || (VOConfig.buttonIconPosition.equals(VOConfig.IconPosition.BOTH) && widget.getX() > scaledWidth/2);

            int color = widget.active ? alphaAndBrightness(alpha, 1.0F) : alphaAndBrightness(alpha, 0.3F);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            int inset = 2;
            if (VOConfig.zoomIconOnHover && widget.isSelected() && widget.active) inset = 1;
            int size = 20-inset*2;

            Identifier textureId = ICONS.get(iconId);

            if (VOConfig.buttonIconPosition.equals(VOConfig.IconPosition.LEFT) || showLeftWhenBoth || (VOConfig.buttonIconPosition.equals(VOConfig.IconPosition.LOCATION) && widget.getX() < scaledWidth/2))
                context.drawTexture(RenderLayer::getGuiTextured, textureId, widget.getX()+inset, widget.getY()+inset, 0, 0, size, size, size, size, size, size, color);

            if (VOConfig.buttonIconPosition.equals(VOConfig.IconPosition.RIGHT) || showRightWhenBoth || (VOConfig.buttonIconPosition.equals(VOConfig.IconPosition.LOCATION) && widget.getX()+widget.getWidth() > scaledWidth/2))
                context.drawTexture(RenderLayer::getGuiTextured, textureId, widget.getX()+widget.getWidth()-20+inset, widget.getY()+inset, 0, 0, size, size, size, size, size, size, color);

            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
        }
    }
    public static void reload(ResourceManager manager) {
        manager.findResources("textures", path -> path.getNamespace().equals("iconic") && path.getPath().contains(".properties")).forEach((id, resource) -> {
            if (manager.getResource(id).isEmpty()) return;
            try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                Identifier iconId = Identifier.of(id.getNamespace(), id.getPath().replace(".properties", ".png"));
                if (manager.getResource(iconId).isPresent()) return;

                Properties properties = new Properties();
                properties.load(stream);
                while (properties.get("properties") != null) {
                    Identifier propertiesId = Identifier.of(properties.getProperty("properties"));
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
                    Identifier textureId = Identifier.tryParse(properties.getProperty("texture"));
                    ICONS.put(iconId, textureId);
                }
            } catch (Exception e) {
                LogManager.getLogger("Iconic").error("Error occurred while loading texture.properties {}", id.toString(), e);
            }
        });
    }
}
