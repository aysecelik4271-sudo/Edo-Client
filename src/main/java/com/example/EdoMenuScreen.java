package com.example;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

public class EdoMenuScreen extends Screen {
    
    private TextFieldWidget searchBox;
    private final List<ModButtonData> allButtons = new ArrayList<>();
    private final List<ButtonWidget> activeWidgets = new ArrayList<>();

    public EdoMenuScreen() {
        super(Text.of("Edo Client VIP Arama Menüsü"));
    }

    @Override
    protected void init() {
        allButtons.clear();
        clearActiveWidgets();

        int searchWidth = 200;
        int searchHeight = 20;
        int searchX = this.width / 2 - searchWidth / 2;
        int searchY = this.height / 2 - 110;

        searchBox = new TextFieldWidget(this.textRenderer, searchX, searchY, searchWidth, searchHeight, Text.of("Mod Ara..."));
        searchBox.setPlaceholder(Text.of("§7Mod adı yazın... (Örn: Fly)"));
        searchBox.setChangedListener(this::onSearchChanged);
        this.addSelectableChild(searchBox);
        searchBox.setFocused(true);

        // 1.21.1 Örnek Havuz Bağlantısı
        allButtons.add(new ModButtonData("Tunnel Finder", "Tunnel Finder: " + (ExampleMod.tunnelBaseFinder ? "§aAÇIK" : "§cKAPALI"), b -> {
            ExampleMod.tunnelBaseFinder = !ExampleMod.tunnelBaseFinder;
            b.setMessage(Text.of("Tunnel Finder: " + (ExampleMod.tunnelBaseFinder ? "§aAÇIK" : "§cKAPALI")));
        }));

        allButtons.add(new ModButtonData("Sus Chunk Finder", "Sus Chunk: " + (ExampleMod.susChunkFinder ? "§aAÇIK" : "§cKAPALI"), b -> {
            ExampleMod.susChunkFinder = !ExampleMod.susChunkFinder;
            b.setMessage(Text.of("Sus Chunk: " + (ExampleMod.susChunkFinder ? "§aAÇIK" : "§cKAPALI")));
        }));

        allButtons.add(new ModButtonData("Fullbright", "Fullbright: " + (ExampleMod.fullbright ? "§aAÇIK" : "§cKAPALI"), b -> {
            ExampleMod.fullbright = !ExampleMod.fullbright;
            b.setMessage(Text.of("Fullbright: " + (ExampleMod.fullbright ? "§aAÇIK" : "§cKAPALI")));
        }));

        refreshButtons("");
    }

    private void onSearchChanged(String query) {
        refreshButtons(query.toLowerCase().trim());
    }

    private void refreshButtons(String filter) {
        clearActiveWidgets();

        int buttonWidth = 150;
        int buttonHeight = 20;
        int startY = this.height / 2 - 75;
        int count = 0;

        for (ModButtonData mod : allButtons) {
            if (filter.isEmpty() || mod.searchName.toLowerCase().contains(filter)) {
                int column = count % 2;
                int row = count / 2;
                int x = (this.width / 2 - 160) + (column * 170);
                int y = startY + (row * 24);

                ButtonWidget btn = ButtonWidget.builder(Text.of(mod.buttonText), mod.action)
                        .dimensions(x, y, buttonWidth, buttonHeight).build();
                
                this.addDrawableChild(btn);
                activeWidgets.add(btn);
                count++;
            }
        }
    }

    private void clearActiveWidgets() {
        for (ButtonWidget widget : activeWidgets) {
            this.remove(widget);
        }
        activeWidgets.clear();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        context.fill(this.width / 2 - 180, this.height / 2 - 140, this.width / 2 + 180, this.height / 2 + 110, 0x95000000);
        context.drawCenteredTextWithShadow(this.textRenderer, "§b★ EDO CLIENT 1.21.1 ★", this.width / 2, this.height / 2 - 132, 0xFFFFFF);
        
        searchBox.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPauseGame() { return false; }

    private static class ModButtonData {
        String searchName;
        String buttonText;
        ButtonWidget.PressAction action;

        public ModButtonData(String searchName, String buttonText, ButtonWidget.PressAction action) {
            this.searchName = searchName;
            this.buttonText = buttonText;
            this.action = action;
        }
    }
}
