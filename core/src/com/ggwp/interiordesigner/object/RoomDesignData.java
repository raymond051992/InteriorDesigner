package com.ggwp.interiordesigner.object;

public class RoomDesignData {

    private static final float DEFAULT_DIMENSION = 100f;

    private String name;
    private float[] vertices;
    private String backgroundImage;

    private float ftHeight;
    private float ftWidth;
    private float ftDepth;

    private float[] leftWallVal, backWallVal, rightWallVal;

    public RoomDesignData(){

    }

    public static RoomDesignData getDefaultInstance(){
        RoomDesignData data = new RoomDesignData();
        data.setVertices(new float[]{
                -DEFAULT_DIMENSION, DEFAULT_DIMENSION, 0,
                0, DEFAULT_DIMENSION, 0,
                DEFAULT_DIMENSION, DEFAULT_DIMENSION, 0,
                DEFAULT_DIMENSION * 2, DEFAULT_DIMENSION, 0,
                -DEFAULT_DIMENSION, 0, 0,
                0, 0, 0,
                DEFAULT_DIMENSION, 0, 0,
                DEFAULT_DIMENSION * 2, 0, 0
        });
        return data;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public float[] getVertices() {
        return vertices;
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float[] getRightWallVal() {
        return rightWallVal;
    }

    public void setRightWallVal(float[] rightWallVal) {
        this.rightWallVal = rightWallVal;
    }

    public float[] getBackWallVal() {
        return backWallVal;
    }

    public void setBackWallVal(float[] backWallVal) {
        this.backWallVal = backWallVal;
    }

    public float[] getLeftWallVal() {
        return leftWallVal;
    }

    public void setLeftWallVal(float[] leftWallVal) {
        this.leftWallVal = leftWallVal;
    }

    public float getFtWidth() {
        return ftWidth;
    }

    public void setFtWidth(float ftWidth) {
        this.ftWidth = ftWidth;
    }

    public float getFtHeight() {
        return ftHeight;
    }

    public void setFtHeight(float ftHeight) {
        this.ftHeight = ftHeight;
    }

    public float getFtDepth() {
        return ftDepth;
    }

    public void setFtDepth(float ftDepth) {
        this.ftDepth = ftDepth;
    }

}