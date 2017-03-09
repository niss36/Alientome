package com.gui;

import com.util.Util;
import com.util.listeners.IntValueListener;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GameSlider extends GameTextComponent implements MouseListener, MouseMotionListener {

    private static final Color border1 = new Color(193, 132, 0);
    private static final Color border2 = new Color(229, 156, 0);
    private static final Color background = new Color(226, 170, 59);

    private static final Color border2Hovered = new Color(226, 169, 54);
    private static final Color backgroundHovered = new Color(224, 184, 109);

    private static final Color backgroundPressed = new Color(221, 197, 150);

    private static final int sliderWidth = 5;

    private final int minValue;
    private final int maxValue;
    private final Function<Integer, String> valueStringProvider;
    private final List<IntValueListener> listeners = new ArrayList<>();
    private int value;
    private boolean hovered;
    private boolean pressed;
    private int sliderX;

    public GameSlider(Dimension dimension, Font font, int minValue, int maxValue, int value, Function<Integer, String> valueStringProvider) {

        super(dimension, "", font);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = value;
        this.valueStringProvider = valueStringProvider;
        setTextToVal();

        setPreferredSize(dimension);
        setMinimumSize(dimension);
        setMaximumSize(dimension);
        setSize(dimension);

        sliderX = computeX();

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    void setTextToVal() {
        setText(valueStringProvider.apply(value));
    }

    public void addValueListener(IntValueListener listener) {
        listeners.add(listener);
    }

    public void setValue(int value) {

        if (value < minValue || value > maxValue)
            throw new IllegalArgumentException("Requested value out of range (" + value + " -> [" + minValue + " - " + maxValue + "])");
        this.value = value;
        setTextToVal();
        sliderX = computeX();
        repaint();
    }

    private int computeX() {
        return (int) Util.scale(value, minValue, maxValue, 4 + sliderWidth / 2, getSliderMaxX());
    }

    private int computeValue() {
        return (int) Util.scale(sliderX, 4 + sliderWidth / 2, getSliderMaxX(), minValue, maxValue);
    }

    private int getSliderMaxX() {
        return getWidth() - 5 - sliderWidth / 2;
    }

    private void onMouseEvent(MouseEvent e, boolean notify) {
        sliderX = e.getX();
        sliderX = Util.clamp(sliderX, 4 + sliderWidth / 2, getSliderMaxX());
        value = computeValue();
        setTextToVal();
        if (notify) notifyStateChange();
        repaint();
    }

    private void notifyStateChange() {
        for (IntValueListener listener : listeners) listener.valueChanged(value);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        FontMetrics metrics = g.getFontMetrics();
        int width = getWidth() - 1;
        int height = getHeight() - 1;

        g.setColor(border1);
        g.drawRoundRect(0, 0, width, height, 6, 6);
        g.drawRect(1, 1, width - 2, height - 2);
        g.setColor(hovered ? border2Hovered : border2);
        g.drawRect(2, 2, width - 4, height - 4);
        g.drawRect(3, 3, width - 6, height - 6);
        g.setColor(pressed ? backgroundPressed : hovered ? backgroundHovered : background);
        g.fillRect(4, 4, width - 7, height - 7);

        g.setColor(Color.black);
        g.fillRect(sliderX - sliderWidth / 2, 4, sliderWidth, height - 7);

        int stringWidth = metrics.stringWidth(getText()) - g.getFont().getSize() / 10;
        int x = (getWidth() - stringWidth) / 2;
        int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent(); //Font baseline

        g.drawString(getText(), x, y);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressed = true;
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = false;
        onMouseEvent(e, true);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        hovered = true;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        hovered = false;
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        onMouseEvent(e, false);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
