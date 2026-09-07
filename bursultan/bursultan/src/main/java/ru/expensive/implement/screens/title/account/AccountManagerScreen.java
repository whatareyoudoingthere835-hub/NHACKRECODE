package ru.expensive.implement.screens.title.account;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.api.system.shape.implement.Rectangle;
import ru.expensive.common.QuickImports;

import ru.expensive.common.util.math.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class AccountManagerScreen extends Screen implements QuickImports {
    public static List<Account> ACCOUNTS = new ArrayList<>();
    private boolean typing;
    String currentUsername = !ACCOUNTS.isEmpty() ? ACCOUNTS.get(0).getUsername() : "";

    public AccountManagerScreen() {
        super(Text.of("Account Manager"));
        AccountManagerConfig.loadCurrentUsername();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        ru.expensive.common.util.render.ExpensiveRenderLayers.beginGuiFrame();
        try {
            renderCustom(context, mouseX, mouseY, delta);
        } finally {
            ru.expensive.common.util.render.ExpensiveRenderLayers.endGuiFrame();
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderCustom(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = MathUtil.getPositionMatrix(context);

        Image image = QuickImports.image.setMatrixStack(context.getMatrices());
        image.setTexture("textures/mainmenu.png").render(ShapeProperties.create(positionMatrix, 0, 0, width, height).build());

        rectangle.render(ShapeProperties.create(positionMatrix, this.width / 2 - 100, this.height / 2 - 20, 200, 20)
                .round(4)
                .softness(1)
                .thickness(2)
                .outlineColor(0xFF2D2E41)
                .color(0xF2141724)
                .build()
        );

        Fonts.getSize(18, Fonts.Type.DEFAULT).drawCenteredString(
                context.getMatrices(),
                typing ? (currentUsername + (typing ? System.currentTimeMillis() % 1000 > 500 ? "_" : "" : "")) : "Введите сюда свой никнейм",
                this.width / 2, this.height / 2 - 13,
                -1
        );

        if (currentUsername.length() == 16) {
            Fonts.getSize(18, Fonts.Type.DEFAULT).drawCenteredString(
                    context.getMatrices(),
                    "Вы превысили допустимый лимит по кол-ву символов (16). Пожайлуста, сделайте ник короче.",
                    this.width / 2, this.height / 2 - 40,
                    0xFFFF0000
            );
        }

        int color = MathUtil.isHovered(mouseX, mouseY, this.width / 2 - 100, this.height / 2 + 40, width, height)
                ? 0xFF232431
                : 0xFF191a28;
        int color2 = MathUtil.isHovered(mouseX, mouseY, this.width / 2 - 100, this.height / 2 + 70, width, height)
                ? 0xFF232431
                : 0xFF191a28;

        new Rectangle().render(ShapeProperties.create(positionMatrix, this.width / 2 - 100, this.height / 2 + 40, 200, 20)
                .round(5)
                .thickness(2)
                .outlineColor(0xFF2d2e41)
                .color(color)
                .build()
        );
        Fonts.getSize(18, Fonts.Type.DEFAULT).drawCenteredString(
                context.getMatrices(),
                "Сохранить аккаунт",
                this.width / 2, this.height / 2 + 46.5f,
                -1
        );

        new Rectangle().render(ShapeProperties.create(positionMatrix, this.width / 2 - 100, this.height / 2 + 70, 200, 20)
                .round(5)
                .thickness(2)
                .outlineColor(0xFF2d2e41)
                .color(color2)
                .build()
        );
        Fonts.getSize(18, Fonts.Type.DEFAULT).drawCenteredString(
                context.getMatrices(),
                "Случайный аккаунт",
                this.width / 2, this.height / 2 + 76.5f,
                -1
        );
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (MathUtil.isHovered(mouseX, mouseY, this.width / 2 - 100, this.height / 2 - 20, 200, 20)) {
            typing = !typing;
        }

        if (MathUtil.isHovered(mouseX, mouseY, this.width / 2 - 100, this.height / 2 + 40, 200, 20)) {
            saveAccounts();
        }

        if (MathUtil.isHovered(mouseX, mouseY, this.width / 2 - 100, this.height / 2 + 70, 200, 20)) {
            String randomNick = NicknameGenerator.generateGameNickname();
            this.currentUsername = randomNick;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        int keyCode = input.key();
        int scanCode = input.scancode();
        int modifiers = input.modifiers();
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setScreen(null);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE && currentUsername.length() > 0) {
            currentUsername = currentUsername.substring(0, currentUsername.length() - 1);
        }

        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            if (!currentUsername.isEmpty()) {
                ACCOUNTS.add(new Account(currentUsername));
            }

            typing = false;
            currentUsername = "";
        }
        return true;
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharInput input) {
        char chr = (char) input.codepoint();
        char codePoint = chr;
        int modifiers = input.modifiers();
        if (currentUsername.length() <= 15) /* на самом деле 16 символов */ currentUsername += Character.toString(codePoint);
        return super.charTyped(input);
    }

    private void saveAccounts() {
        ACCOUNTS.clear();
        ACCOUNTS.add(new Account(currentUsername));

        AccountManagerConfig.saveAccounts(currentUsername);
        this.client.setScreen(null);
    }
}