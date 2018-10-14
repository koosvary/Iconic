/**
 * Copyright (C) 2018 Iconic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.iconic.control;

import javafx.beans.NamedArg;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LabelledSlider extends VBox {
    private final Slider slider;
    private final Label label;
    private final String slug;

    public LabelledSlider(@NamedArg("text") String slug) {
        super();

        this.slug = slug;
        label = new Label();
        label.setText(this.slug + ": 10%");

        slider = new Slider();
        slider.setValue(0.1f);
        slider.setMax(1.f);
        slider.setMin(0.f);
        slider.setBlockIncrement(0.01);
        slider.setMajorTickUnit(0.1f);
        slider.setShowTickLabels(false);
        slider.setShowTickMarks(true);

        slider.valueProperty().addListener((source, oldValue, newValue) -> {
            int percent = (int) (slider.getValue() * 100);

            label.textProperty().setValue(
                    getSlug() + ": " + String.format("%d%%", percent)
            );
        });

        label.setLabelFor(slider);

        setSpacing(10);
        setPrefWidth(400.f);
        setAlignment(Pos.CENTER);
        getChildren().add(label);
        getChildren().add(slider);
    }

    public Slider getSlider() {
        return slider;
    }

    public String getSlug() {
        return slug;
    }
}
