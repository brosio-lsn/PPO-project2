package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
/**
 * represents a point of passage
 *
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */


/**
 * @param point position of the point of passage in the swiss coordinate system
 * @param closestNodeId the identity of the JaVelo node closest to the point
 */
public record WayPoint(PointCh point, int closestNodeId) {
}
