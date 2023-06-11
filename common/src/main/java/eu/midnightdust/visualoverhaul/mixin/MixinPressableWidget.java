package eu.midnightdust.visualoverhaul.mixin;

import eu.midnightdust.visualoverhaul.IconicButtons;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PressableWidget.class)
public abstract class MixinPressableWidget extends ClickableWidget {
    @Unique IconicButtons iconicButtons;
    public MixinPressableWidget(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }
    @Inject(at = @At("TAIL"), method = "<init>")
    private void iconic$onInitButton(int i, int j, int k, int l, Text text, CallbackInfo ci) {
        iconicButtons = new IconicButtons(this);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/PressableWidget;drawMessage(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;I)V", shift = At.Shift.BEFORE), method = "renderButton")
    private void iconic$onRenderButton(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        iconicButtons.renderIcons(context, this, this.alpha);
    }
}
