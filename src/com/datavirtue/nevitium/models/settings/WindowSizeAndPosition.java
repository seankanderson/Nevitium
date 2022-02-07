package com.datavirtue.nevitium.models.settings;

import java.awt.Dimension;
import java.awt.Point;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
public class WindowSizeAndPosition {
    Point location;
    Dimension size;    
    double splitFactor;
}
