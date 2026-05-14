package no.ntnu.idatt2003.view.components;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Component for displaying a stock price chart.
 *
 * <p>Renders a simple line chart based on a list
 * of historical stock prices.</p>
 *
 * <p>The chart is drawn on a JavaFX {@link Canvas}
 * and automatically scales values between the minimum
 * and maximum price in the provided dataset.</p>
 */
public class PriceChartComponent extends Canvas {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 300;

    /**
     * Creates a new price chart component.
     *
     * @param prices the historical stock prices to display
     */
    public PriceChartComponent(List<BigDecimal> prices) {
        super(WIDTH, HEIGHT);
        draw(prices);
    }

    /**
     * Draws the price chart on the canvas.
     * 
     * <p>The method clears the canvas, calculates scaling based on the minimum prices,
     * and displays the price history as a connected line graph.</p>
     *
     * <p>If fewer than two prices are available, the chart will not be drawn.</p>
     *
     * @param prices the historical stock prices to display
     */
    private void draw(List<BigDecimal> prices) {
        GraphicsContext gc = getGraphicsContext2D();

        gc.clearRect(0, 0, WIDTH, HEIGHT);
        gc.strokeRect(0, 0, WIDTH, HEIGHT);

        if (prices == null || prices.size() < 2) {
            return;
        }

        BigDecimal max = Collections.max(prices);
        BigDecimal min = Collections.min(prices);

        double maxValue = max.doubleValue();
        double minValue = min.doubleValue();

        double xStep = (double) WIDTH / (prices.size() - 1);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);

        gc.beginPath();

        for (int i = 0; i < prices.size(); i++) {
            double price = prices.get(i).doubleValue();

            double x = i * xStep;
            double y = HEIGHT - ((price - minValue) / (maxValue - minValue)) * HEIGHT;

            if (i == 0) {
                gc.moveTo(x, y);
            } else {
                gc.lineTo(x, y);
            }
        }

        gc.stroke();
    }
}