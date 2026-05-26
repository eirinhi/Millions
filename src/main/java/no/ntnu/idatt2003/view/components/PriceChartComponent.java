package no.ntnu.idatt2003.view.components;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Component for displaying a stock price chart.
 *
 * <p>Renders a line chart with a y-axis price scale on the left
 * and x-axis week numbers along the bottom.
 * Green and red dots mark the highest and lowest prices.</p>
 */
public class PriceChartComponent extends Canvas {

    /** Default canvas width in pixels. */
    private static final double DEFAULT_WIDTH  = 400;

    /** Default canvas height in pixels. */
    private static final double DEFAULT_HEIGHT = 222;

    /** Left padding reserved for y-axis labels. */
    private static final double LEFT_PAD  = 58;

    /** Right padding inside the plot area. */
    private static final double RIGHT_PAD = 10;

    /** Top padding inside the plot area. */
    private static final double TOP_PAD   = 10;

    /** Bottom padding reserved for x-axis labels. */
    private static final double BOT_PAD   = 24;

    /** Radius of the min/max highlight dots. */
    private static final double DOT_R     = 5;

    /** Number of y-axis tick intervals. */
    private static final int    Y_TICKS   = 3;

    /** Maximum number of x-axis labels shown. */
    private static final int    X_LABELS  = 5;

    /** Colour used for the maximum price dot and label. */
    private static final Color COLOR_MAX  = Color.web("#2bbd6e");

    /** Colour used for the minimum price dot and label. */
    private static final Color COLOR_MIN  = Color.web("#e34c4c");

    /** Corner arc radius for the rounded background rectangle. */
    private static final double CORNER_ARC = 10;

    /** Font size used for axis labels. */
    private static final double FONT_SIZE = 10;

    /** Horizontal distance from the plot edge to the y-axis label anchor. */
    private static final double Y_LABEL_X_OFFSET = 7;

    /** Vertical offset added to each y-axis label for visual centring. */
    private static final double Y_LABEL_Y_OFFSET = 4;

    /** Vertical distance below the plot bottom for x-axis labels. */
    private static final double X_LABEL_Y_OFFSET = 15;

    /** Price threshold above which labels are shown without decimals. */
    private static final double PRICE_INTEGER_THRESHOLD = 1000;

    /** The historical prices rendered by this chart. */
    private List<BigDecimal> prices;

    /**
     * Creates a new price chart component.
     *
     * @param priceList the historical stock prices to display
     */
    public PriceChartComponent(final List<BigDecimal> priceList) {
        super(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.prices = priceList;
        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());
        draw();
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double minWidth(double height) {
        return 0;
    }

    @Override
    public double minHeight(double width) {
        return 0;
    }

    /**
     * Redraws the chart with updated prices.
     *
     * @param priceList the updated historical stock prices
     */
    public void updatePrices(final List<BigDecimal> priceList) {
        this.prices = priceList;
        draw();
    }

    /**
     * Draws the price chart on the canvas.
     */
    private void draw() {
        double w = getWidth();
        double h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);

        double arc = CORNER_ARC;
        gc.save();
        gc.beginPath();
        gc.moveTo(arc, 0);
        gc.lineTo(w - arc, 0);
        gc.arcTo(w, 0, w, arc, arc);
        gc.lineTo(w, h - arc);
        gc.arcTo(w, h, w - arc, h, arc);
        gc.lineTo(arc, h);
        gc.arcTo(0, h, 0, h - arc, arc);
        gc.lineTo(0, arc);
        gc.arcTo(0, 0, arc, 0, arc);
        gc.closePath();
        gc.clip();

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, w, h);

        if (prices == null || prices.isEmpty()) {
            gc.restore();
            return;
        }

        List<BigDecimal> drawPrices = prices.size() < 2
            ? List.of(prices.get(0), prices.get(0))
            : prices;

        double maxVal = Collections.max(drawPrices).doubleValue();
        double minVal = Collections.min(drawPrices).doubleValue();
        boolean hasRealExtremes = prices.size() >= 2
            && Double.compare(maxVal, minVal) != 0;
        if (!hasRealExtremes) {
            maxVal += 1;
            minVal -= 1;
        }

        double plotX = LEFT_PAD;
        double plotY = TOP_PAD;
        double plotW = w - LEFT_PAD - RIGHT_PAD;
        double plotH = h - TOP_PAD - BOT_PAD;
        double xStep = plotW / (drawPrices.size() - 1);

        drawYAxis(gc, plotX, plotY, plotH, maxVal, minVal, hasRealExtremes);
        drawXAxis(gc, plotX, plotY, plotH, xStep, prices.size());

        drawLine(gc, plotH, xStep, maxVal, minVal, drawPrices);
        if (hasRealExtremes) {
            drawExtremes(gc, plotH, xStep, maxVal, minVal, drawPrices);
        }

        gc.restore();
    }

    /**
     * Draws the y-axis labels on the left side of the plot area.
     *
     * @param gc       the graphics context
     * @param plotX    the x-coordinate of the plot area
     * @param plotY    the y-coordinate of the plot area
     * @param plotH    the height of the plot area
     * @param maxVal   the maximum sales price
     * @param minVal   the minimum sales price
     * @param showExtremeColors  whether to show colors for extreme values
     */
    private void drawYAxis(
            final GraphicsContext gc,
            final double plotX,
            final double plotY,
            final double plotH,
            final double maxVal,
            final double minVal,
            final boolean showExtremeColors) {

        gc.setFont(Font.font(FONT_SIZE));
        gc.setTextAlign(TextAlignment.RIGHT);

        for (int t = 0; t <= Y_TICKS; t++) {
            double fraction = (double) t / Y_TICKS;
            double price    = maxVal - fraction * (maxVal - minVal);
            double y        = plotY + plotH * fraction;

            if (showExtremeColors && t == 0) {
                gc.setFill(COLOR_MAX);
            } else if (showExtremeColors && t == Y_TICKS) {
                gc.setFill(COLOR_MIN);
            } else {
                gc.setFill(Color.DARKGRAY);
            }

            gc.fillText(
                formatPrice(price),
                plotX - Y_LABEL_X_OFFSET,
                y + Y_LABEL_Y_OFFSET
            );
        }

        gc.setTextAlign(TextAlignment.LEFT);
    }

    /**
     * Draws the x-axis labels at the bottom of the plot area.
     *
     * @param gc       the graphics context
     * @param plotX    the x-coordinate of the plot area
     * @param plotY    the y-coordinate of the plot area
     * @param plotH    the height of the plot area
     * @param xStep    the step size for the x-axis
     * @param n        the total number of data points
     */
    private void drawXAxis(
            final GraphicsContext gc,
            final double plotX,
            final double plotY,
            final double plotH,
            final double xStep,
            final int n) {
        int step = Math.max(1, (int) Math.ceil((double) (n - 1) / X_LABELS));

        gc.setFont(Font.font(FONT_SIZE));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.DARKGRAY);

        for (int i = 0; i < n; i += step) {
            double x = plotX + xStep * i;
            gc.fillText(
                String.valueOf(i + 1),
                x,
                plotY + plotH + X_LABEL_Y_OFFSET
            );
        }

        int last = n - 1;
        if (last % step != 0) {
            double x = plotX + xStep * last;
            gc.fillText(
                String.valueOf(n),
                x,
                plotY + plotH + X_LABEL_Y_OFFSET
            );
        }

        gc.setTextAlign(TextAlignment.LEFT);
    }

    /**
     * Draws a line connecting the data points in the plot area.
     *
     * @param gc       the graphics context
     * @param plotH    the height of the plot area
     * @param xStep    the step size for the x-axis
     * @param maxVal   the maximum sales price
     * @param minVal   the minimum sales price
     * @param drawPrices the list of prices to draw
     */
    private void drawLine(
            final GraphicsContext gc,
            final double plotH,
            final double xStep,
            final double maxVal,
            final double minVal,
            final List<BigDecimal> drawPrices) {

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.beginPath();

        for (int i = 0; i < drawPrices.size(); i++) {
            double v = drawPrices.get(i).doubleValue();
            double x = LEFT_PAD + xStep * i;
            double y = TOP_PAD
                + plotH * (1 - (v - minVal) / (maxVal - minVal));

            if (i == 0) {
                gc.moveTo(x, y);
            } else {
                gc.lineTo(x, y);
            }
        }

        gc.stroke();
    }

    /**
     * Draws the extreme points (highest and lowest) on the plot.
     *
     * @param gc       the graphics context
     * @param plotH    the height of the plot area
     * @param xStep    the step size for the x-axis
     * @param maxVal   the maximum sales price
     * @param minVal   the minimum sales price
     * @param drawPrices the list of prices to draw
     */
    private void drawExtremes(
            final GraphicsContext gc,
            final double plotH,
            final double xStep,
            final double maxVal,
            final double minVal,
            final List<BigDecimal> drawPrices) {

        int hiIdx = 0;
        int loIdx = 0;
        for (int i = 0; i < drawPrices.size(); i++) {
            double v = drawPrices.get(i).doubleValue();
            if (Double.compare(v, maxVal) == 0) {
                hiIdx = i;
            }
            if (Double.compare(v, minVal) == 0) {
                loIdx = i;
            }
        }

        double xHi = LEFT_PAD + xStep * hiIdx;
        double yHi = TOP_PAD;
        double xLo = LEFT_PAD + xStep * loIdx;
        double yLo = TOP_PAD + plotH;

        gc.setFill(COLOR_MAX);
        gc.fillOval(xHi - DOT_R, yHi - DOT_R, 2 * DOT_R, 2 * DOT_R);

        gc.setFill(COLOR_MIN);
        gc.fillOval(xLo - DOT_R, yLo - DOT_R, 2 * DOT_R, 2 * DOT_R);
    }

    /**
     * Formats the price for display.
     *
     * @param price the price to format
     * @return the formatted price string
     */
    private String formatPrice(final double price) {
        if (price >= PRICE_INTEGER_THRESHOLD) {
            return String.format("%.0f", price);
        }
        return String.format("%.1f", price);
    }
}
