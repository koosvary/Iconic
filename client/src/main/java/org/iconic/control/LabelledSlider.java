/**
 * Copyright 2018 Iconic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
