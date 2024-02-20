package group.battleship.ui;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Style {

    public static final int SPACING_DEFAULT = 5;
    public static final int SPACING_LARGE = 50;
    public static final Insets INSETS_DEFAULT = new Insets(5, 5, 5, 5);
    public static final Insets INSETS_LARGE = new Insets(50, 50, 50, 50);
    public static final Font FONT_DEFAULT = new Font("Arial", 24);
    public static final Font FONT_SMALL = new Font("Arial", 12);
    public static final int MIN_LAYOUT_WIDTH = 600;
    public static final int MIN_LAYOUT_HEIGHT = 600;
    public static final BorderStroke BORDER_LIGHT_GRAY = new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT);
    public static final BorderStroke BORDER_BLACK = new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT);
    public static final BorderStroke BORDER_DARK_BLUE = new BorderStroke(Color.ROYALBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT);

}
