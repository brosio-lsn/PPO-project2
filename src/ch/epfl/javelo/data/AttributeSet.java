package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.SwissBounds;

import java.util.StringJoiner;

public record AttributeSet(long bits) {
    public AttributeSet{
        Preconditions.checkArgument(bits >>> Attribute.COUNT==0);
    }

    public static AttributeSet of(Attribute... attributes){
        long bits =0;
        for(Attribute a : attributes) bits=bits | 1L << a.ordinal();
        return new AttributeSet(bits);
    }

    public boolean contains(Attribute attribute){
        long mask = 1L << attribute.ordinal();
        return ((this.bits & mask)==mask);
    }

    public boolean intersects(AttributeSet that){
        return ((this.bits & that.bits)!=0);
    }

    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(", ", "{", "}");
        for(Attribute a : Attribute.ALL) if(contains(a)) j.add(a.toString());
        return j.toString();
    }
}
