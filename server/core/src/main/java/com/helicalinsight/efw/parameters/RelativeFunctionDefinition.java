package com.helicalinsight.efw.parameters;

/**
 * One relative-date function from the parameter catalog.
 * The evaluator applies these fields. It does not branch on the function name.
 */
public final class RelativeFunctionDefinition {

    private final String name;
    private final String unit;
    private final String boundary;
    private final int offset;
    private final String direction;
    private final boolean acceptsModifier;
    private final boolean inheritsUnit;
    private final int multiplier;

    public RelativeFunctionDefinition(String name, String unit, String boundary, int offset, String direction,
            boolean acceptsModifier, boolean inheritsUnit, int multiplier) {
        this.name = name;
        this.unit = unit;
        this.boundary = boundary;
        this.offset = offset;
        this.direction = direction;
        this.acceptsModifier = acceptsModifier;
        this.inheritsUnit = inheritsUnit;
        this.multiplier = multiplier;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public String getBoundary() {
        return boundary;
    }

    public int getOffset() {
        return offset;
    }

    public String getDirection() {
        return direction;
    }

    public boolean isAcceptsModifier() {
        return acceptsModifier;
    }

    public boolean isInheritsUnit() {
        return inheritsUnit;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public boolean isNow() {
        return "NOW".equals(boundary);
    }
}
