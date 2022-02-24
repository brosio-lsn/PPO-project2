package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.SwissBounds;
import java.util.StringJoiner;

public record AttributeSet(long bits) {
    public AttributeSet{
        Preconditions.checkArgument(bits >>> Attribute.COUNT==0);
    }
    /**
     * returns the attributeSet corresponding to the attributes given in argument.
     * @param attributes attributes to be translated into an attributeSet.
     * @return the attributeSet corresponding to the attributes given in argument.
     */
    public static AttributeSet of(Attribute... attributes){
        long bits =0;
        for(Attribute a : attributes) bits=bits | 1L << a.ordinal();
        return new AttributeSet(bits);
    }

    /**
     * Indicates whether this AttributeSet contains the attribute given in the parameter of the function.
     * @param attribute attribute to check if it is contained in this AttributeSet
     * @return true if the attribute is contained in this AttributeSet, false otherwise.
     */
    public boolean contains(Attribute attribute){
        long mask = 1L << attribute.ordinal();
        return ((this.bits & mask)==mask);
    }
    /**
     * indicates whether this AttributeSet and that AttributeSet share common attributes.
     * @param that AttributeSet to be compared with.
     * @return true if this and that share common attributes, false else.
     */
    public boolean intersects(AttributeSet that){
        return ((this.bits & that.bits)!=0);
    }

    /**
     * Prints the AttributeSet in such a way that each attribute in the AttributeSet is explicitly displayed.
     * @return the string which displays each attribute in braces, separated by comas in the order of their
     * position in the enumeration.
     */
    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(", ", "{", "}");
        for(Attribute a : Attribute.ALL) if(contains(a)) j.add(a.toString());
        return j.toString();
    }
}