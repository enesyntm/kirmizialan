package net.yntm.kirmizialan.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.yntm.kirmizialan.logic.AreaManager;
import net.yntm.kirmizialan.logic.TileManager;
import org.jetbrains.annotations.NotNull;

public class TileHudRenderer implements HudRenderCallback {

    private static final int CYAN  = 0xFF00FFFF;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int RED   = 0xFFFF5555;
    private static final int GREEN = 0xFF55FF55;

    private static final int BOX_WIDTH = 86;
    private static final int PADDING   = 8;
    private static final int LINE_H    = 10;

    @Override
    @SuppressWarnings("deprecation")
    public void onHudRender(@NotNull DrawContext ctx, @NotNull RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        int screenH = client.getWindow().getScaledHeight();

        int boxX = 10;
        int boxY = screenH / 2 - 45;

        int available = TileManager.getTiles();
        int unlocked  = AreaManager.getUnlocked().size();

        String availableStr = String.valueOf(available);
        String unlockedStr  = String.valueOf(unlocked);

        int textWidth = BOX_WIDTH - PADDING * 2;

        // Satır sayısını hesapla
        int availableLines = countLines(client, availableStr, textWidth);
        int unlockedLines  = countLines(client, unlockedStr,  textWidth);

        // Toplam kutu yüksekliği
        int boxHeight = PADDING                     // üst boşluk
                + LINE_H + 4                        // "Tileman" başlık
                + LINE_H + 2                        // "Available Tiles:"
                + availableLines * LINE_H + 6       // available değer
                + LINE_H + 2                        // "Unlocked Tiles:"
                + unlockedLines * LINE_H             // unlocked değer
                + PADDING;                          // alt boşluk

        // Arka plan
        ctx.fill(boxX - PADDING, boxY - PADDING,
                boxX - PADDING + BOX_WIDTH, boxY - PADDING + boxHeight,
                0x55000000);

        int cx = boxX - PADDING + BOX_WIDTH / 2; // merkez x
        int y  = boxY;

        // Başlık — ortalı
        drawCentered(ctx, client, "Tileman", cx, y, CYAN);
        y += LINE_H + 4;

        // Available Tiles label — ortalı
        drawCentered(ctx, client, "Available Tiles:", cx, y, WHITE);
        y += LINE_H + 2;

        // Available değer — ortalı, çok satırlı
        int availableColor = available > 0 ? GREEN : RED;
        y = drawCenteredWrapped(ctx, client, availableStr, cx, y, textWidth, availableColor);
        y += 6;

        // Unlocked Tiles label — ortalı
        drawCentered(ctx, client, "Unlocked Tiles:", cx, y, WHITE);
        y += LINE_H + 2;

        // Unlocked değer — ortalı, çok satırlı
        drawCenteredWrapped(ctx, client, unlockedStr, cx, y, textWidth, GREEN);
    }

    private void drawCentered(DrawContext ctx, MinecraftClient client,
                              String text, int cx, int y, int color) {
        int tw = client.textRenderer.getWidth(text);
        ctx.drawText(client.textRenderer, Text.literal(text), cx - tw / 2, y, color, false);
    }

    private int drawCenteredWrapped(DrawContext ctx, MinecraftClient client,
                                    String text, int cx, int y, int maxWidth, int color) {
        // Metni maxWidth'e sığacak şekilde böl
        java.util.List<String> lines = new java.util.ArrayList<>();
        String remaining = text;

        while (!remaining.isEmpty()) {
            int cutoff = client.textRenderer.trimToWidth(remaining, maxWidth).length();
            if (cutoff == 0) cutoff = 1;
            lines.add(remaining.substring(0, cutoff));
            remaining = remaining.substring(cutoff);
        }

        for (String line : lines) {
            int tw = client.textRenderer.getWidth(line);
            ctx.drawText(client.textRenderer, Text.literal(line), cx - tw / 2, y, color, false);
            y += LINE_H;
        }

        return y;
    }

    private int countLines(MinecraftClient client, String text, int maxWidth) {
        if (text.isEmpty()) return 1;
        int lines = 0;
        String remaining = text;
        while (!remaining.isEmpty()) {
            int cutoff = client.textRenderer.trimToWidth(remaining, maxWidth).length();
            if (cutoff == 0) cutoff = 1;
            remaining = remaining.substring(cutoff);
            lines++;
        }
        return Math.max(1, lines);
    }
}