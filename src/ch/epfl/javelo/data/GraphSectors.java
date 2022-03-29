package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * sectors of the graph
 *
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */

/**
 * the buffer contains all the value of all attributes from all sectors
 */
public record GraphSectors(ByteBuffer buffer) {

    private static final int OFFSET_IDENTITY_OF_FIRST_NODE = 0;
    private static final int OFFSET_NUMBER_OF_NODES = OFFSET_IDENTITY_OF_FIRST_NODE+4;
    private static final int SECTOR_BYTES = OFFSET_NUMBER_OF_NODES+2;
    private static final int NUMBER_OF_SECTORS_ON_SIDE=128;
    private static final double SECTOR_WIDTH =SwissBounds.WIDTH/NUMBER_OF_SECTORS_ON_SIDE;
    private static final double SECTOR_HEIGHT =SwissBounds.HEIGHT/NUMBER_OF_SECTORS_ON_SIDE;

    /**
     * returns the list of all sectors having an intersection with a scare defined by its given center and length side
     * @param center the center of the scare
     * @param distance half the length of a side
     * @return the list of all sectors having an intersection with the scare
     */
    public List<Sector> sectorsInArea(PointCh center, double distance){

        List<Sector> sectors= new ArrayList<>();

        int xCoordinateOfBottomLeftSector = Math2.clamp(1, (int)Math.ceil(((center.e()-distance)-SwissBounds.MIN_E)/SECTOR_WIDTH), NUMBER_OF_SECTORS_ON_SIDE);
        int xCoordinateOfBottomRightSector = Math2.clamp(1, (int)Math.ceil(((center.e()+distance)-SwissBounds.MIN_E)/SECTOR_WIDTH), NUMBER_OF_SECTORS_ON_SIDE);;
        int yCoordinateOfBottomLeftSector = Math2.clamp(1, (int)Math.ceil(((center.n()-distance)-SwissBounds.MIN_N)/SECTOR_HEIGHT), NUMBER_OF_SECTORS_ON_SIDE);
        int yCoordinateOfTopLeftSector = Math2.clamp(1, (int)Math.ceil(((center.n()+distance)-SwissBounds.MIN_N)/SECTOR_HEIGHT), NUMBER_OF_SECTORS_ON_SIDE);

        for(int y=yCoordinateOfBottomLeftSector; y<=yCoordinateOfTopLeftSector; ++y )
            for (int x = xCoordinateOfBottomLeftSector; x <= xCoordinateOfBottomRightSector; ++x) {
                int idOfSector = NUMBER_OF_SECTORS_ON_SIDE*(y-1)+(x-1);
                sectors.add(getSectorAtIdentity(idOfSector));
            }
        return sectors;
    }

    /**
     * returns the sectors given its identity
     * @param identity the identity of the sector
     * @return the sector of given identity
     */
    private Sector getSectorAtIdentity(int identity) {
        //initiated startNodeId here to not calculate it twice
        //TODO ask if for startNodeId we should use extractnsigned
        int startNodeId= (buffer.getInt(identity*SECTOR_BYTES+OFFSET_IDENTITY_OF_FIRST_NODE));
        int endNodeId= startNodeId + Short.toUnsignedInt(buffer.getShort(identity*SECTOR_BYTES+OFFSET_NUMBER_OF_NODES));
        return new Sector(startNodeId, endNodeId);
    }

    /**
     * public inner record that represents a sector
     * @param startNodeId the index of the sector's first node
     * @param endNodeId the index of the node just after the sector's last node
     */
    public record Sector(int startNodeId, int endNodeId) {
    }
}
