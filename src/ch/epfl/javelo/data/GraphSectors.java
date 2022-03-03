package ch.epfl.javelo.data;

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

//TODO ASK ASSISTANT TO SEE IF RIGHT METHOD
//les secteurs de base en bidimension (avec coordonnee x et y) sont stockéS en unidimentionnel dans le buffer
//but premier :  touver quels secteurs sont sur les extrimites du carré (tu peux le faire en prenant la taille des secteurs)
//prendre ensuite tous les secteurs entre ces 4 secteurs (le offset sert ä acceder au premier index du byte auquel on veut acceder parmi les byte du secteur considéré

/**
 * the buffer contains all the value of all attributes from all sectors
 */
public record GraphSectors(ByteBuffer buffer) {

    private static final int OFFSET_IDENTITY_OF_FIRST_NODE = 0;
    private static final int OFFSET_NUMBER_OF_NODES = OFFSET_IDENTITY_OF_FIRST_NODE+4;
    private static final int SECTOR_BYTES = OFFSET_NUMBER_OF_NODES+2;
    //TODO ASK BETTER CONSTANTS OR METHODS?
    private static final int NUMBER_OF_SECTORS=16384;
    private static final int NUMBER_OF_SECTORS_ON_SIDE=(int)Math.sqrt(NUMBER_OF_SECTORS);//128
    private static final double SECTOR_WIDTH =SwissBounds.WIDTH/NUMBER_OF_SECTORS_ON_SIDE;
    private static final double SECTOR_HEIGHT =SwissBounds.HEIGHT/NUMBER_OF_SECTORS_ON_SIDE;

    //private int numberOfSectors(){return buffer.capacity()/6;}
    /*private double SectorsWidth(){return SwissBounds.WIDTH/Math.sqrt(NUMBER_OF_SECTORS);}
    private double SectorsHeight(){return SwissBounds.HEIGHT/Math.sqrt(NUMBER_OF_SECTORS);}*/

    /**
     * returns the list of all sectors having an intersection with a scare defined by its given center and length side
     * @param center the center of the scare
     * @param distance the length of a side
     * @return the list of all sectors having an intersection with the scare
     */
    public List<Sector> sectorsInArea(PointCh center, double distance){
        //TODO voir si (center.n()-distance/2)-SwissBounds.MIN_N pas negaatif
        ArrayList<Sector> sectors= new ArrayList<>();
        int xCoordinateOfBottomLeftSector = (int)Math.ceil(((center.n()-distance/2)-SwissBounds.MIN_N)/SECTOR_WIDTH);
        //int yCoordinateOfBottomLeftSector = (int)Math.ceil(((center.e()-distance/2)-SwissBounds.MIN_E)/SECTOR_HEIGHT);
        //int identityOfBottomLeftSector= 128*(yCoordinateOfBottomLeftSector-1)+xCoordinateOfBottomLeftSector-1;

        int xCoordinateOfBottomRightSector = (int)Math.ceil(((center.n()-distance/2)-SwissBounds.MIN_N)/SECTOR_WIDTH);
        //int yCoordinateOfBottomRightSector = (int)Math.ceil(((center.e()+distance/2)-SwissBounds.MIN_E)/SECTOR_HEIGHT);
        //int identityOfBottomRightSector= 128*(yCoordinateOfBottomRightSector-1)+xCoordinateOfBottomRightSector-1;

        int xCoordinateOfTopRightSector = (int)Math.ceil(((center.n()+distance/2)-SwissBounds.MIN_N)/SECTOR_WIDTH);
        //int yCoordinateOfTopRightSector = (int)Math.ceil(((center.e()+distance/2)-SwissBounds.MIN_E)/SECTOR_HEIGHT);
        //int identityOfTopRightSector= 128*(yCoordinateOfTopRightSector-1)+xCoordinateOfTopRightSector-1;

        int xCoordinateOfTopLeftSector = (int)Math.ceil(((center.n()-distance/2)-SwissBounds.MIN_N)/SECTOR_WIDTH);
        //int yCoordinateOfTopLeftSector = (int)Math.ceil(((center.e()+distance/2)-SwissBounds.MIN_E)/SECTOR_HEIGHT);
        //int identityOfTopLeftSector= 128*(yCoordinateOfTopLeftSector-1)+xCoordinateOfTopLeftSector-1;

/*        sectors.add(getSectorAtIdentity(identityOfBottomLeftSector));
        sectors.add(getSectorAtIdentity(identityOfBottomRightSector));
        sectors.add(getSectorAtIdentity(identityOfTopLeftSector));
        sectors.add(getSectorAtIdentity(identityOfTopRightSector));*/

        for(int x=xCoordinateOfBottomLeftSector; x<=xCoordinateOfBottomRightSector; ++x ){
            for(int y=xCoordinateOfTopLeftSector; y<=xCoordinateOfTopRightSector; ++y )
                sectors.add(getSectorAtIdentity(128*(y-1)+x-1));
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
        int startNodeId=buffer.getInt(identity*SECTOR_BYTES+OFFSET_IDENTITY_OF_FIRST_NODE);
        return new Sector(startNodeId, startNodeId + buffer.getShort(identity*SECTOR_BYTES+OFFSET_NUMBER_OF_NODES));
    }

    /**
     * private inner record that represents a sector
     * @param startNodeId the index of the sector's first node
     * @param endNodeId the index of the node just after the sector's last node
     */
    private record Sector(int startNodeId, int endNodeId) {
    }
}
