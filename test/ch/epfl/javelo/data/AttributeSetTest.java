package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttributeSetTest {

    @Test
    void ofTrivialAttribute() {
        Attribute[] A = new Attribute[]{Attribute.HIGHWAY_SERVICE};
        assertEquals(new AttributeSet(0b1L), AttributeSet.of(A));
    }

    @Test
    void containsKnownTrivialAttribute() {
        Attribute[] A = new Attribute[]{Attribute.HIGHWAY_SERVICE};
        assertTrue(AttributeSet.of(A).contains(Attribute.HIGHWAY_SERVICE));
    }

    @Test
    void doesNotContainKnownAttribute() {
        Attribute[] B = new Attribute[]{Attribute.HIGHWAY_CYCLEWAY};
        assertFalse(AttributeSet.of(B).contains(Attribute.HIGHWAY_TRACK));
    }

    @Test
    void knownTrivialAttributesIntersect() {
        AttributeSet A = new AttributeSet(0b1101111010101010101011110010101011L);
        AttributeSet B = new AttributeSet(0b011L);
        AttributeSet notB = new AttributeSet(0b100L);
        assertTrue(A.intersects(B));
    }

    @Test
    void knownTrivialAttributesDoNotIntersect() {
        AttributeSet B = new AttributeSet(0b011L);
        AttributeSet notB = new AttributeSet(0b100L);
        assertFalse(B.intersects(notB));
    }

    @Test
    void testToString() {
        AttributeSet set = AttributeSet.of(Attribute.TRACKTYPE_GRADE1, Attribute.HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}", set.toString());
    }

    @Test
    void bits() {

    }

    @Test
    void throwsExceptionWhenIllegalArgument() {
        System.out.println(~0b1L<<63);
        assertThrows(IllegalArgumentException.class, () -> {
            new AttributeSet(~0b1L);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new AttributeSet(0b1L<<63);
        });
    }
}