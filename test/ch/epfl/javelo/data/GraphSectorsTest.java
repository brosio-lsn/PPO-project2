package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;
import ch.epfl.javelo.data.GraphSectors;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphSectorsTest {



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

        private static final int OFFSET_IDENTITY_OF_FIRST_NODE = 0;
        private static final int OFFSET_NUMBER_OF_NODES = OFFSET_IDENTITY_OF_FIRST_NODE+4;
        private static final int SECTOR_BYTES = OFFSET_NUMBER_OF_NODES+2;
        //TODO ASK BETTER CONSTANTS OR METHODS?
        private static final int NUMBER_OF_SECTORS=64;
        private static final int NUMBER_OF_SECTORS_ON_SIDE=(int)Math.sqrt(NUMBER_OF_SECTORS);//128
        private static final double SECTOR_WIDTH = 1;
        private static final double SECTOR_HEIGHT =1;

        //private int numberOfSectors(){return buffer.capacity()/6;}
    /*private double SectorsWidth(){return SwissBounds.WIDTH/Math.sqrt(NUMBER_OF_SECTORS);}
    private double SectorsHeight(){return SwissBounds.HEIGHT/Math.sqrt(NUMBER_OF_SECTORS);}*/
        ByteBuffer buffer = ByteBuffer.allocate(64*6);
        /**
         * returns the list of all sectors having an intersection with a scare defined by its given center and length side
         * @param center the center of the scare
         * @param distance the length of a side
         * @return the list of all sectors having an intersection with the scare
         */
        public List<ch.epfl.javelo.data.GraphSectors.Sector> sectorsInArea(PointCh center, double distance){
            //TODO voir si (center.n()-distance/2)-SwissBounds.MIN_N pas negaatif
            ArrayList<ch.epfl.javelo.data.GraphSectors.Sector> sectors= new ArrayList<>();
            int xCoordinateOfBottomLeftSector = (int)Math.ceil(((3-distance/2)-0)/SECTOR_WIDTH);
            //int yCoordinateOfBottomLeftSector = (int)Math.ceil(((center.e()-distance/2)-SwissBounds.MIN_E)/SECTOR_HEIGHT);
            //int identityOfBottomLeftSector= 128*(yCoordinateOfBottomLeftSector-1)+xCoordinateOfBottomLeftSector-1;

            int xCoordinateOfBottomRightSector = (int)Math.ceil(((3+distance/2)-0)/SECTOR_WIDTH);
            //int yCoordinateOfBottomRightSector = (int)Math.ceil(((center.e()+distance/2)-SwissBounds.MIN_E)/SECTOR_HEIGHT);
            //int identityOfBottomRightSector= 128*(yCoordinateOfBottomRightSector-1)+xCoordinateOfBottomRightSector-1;

            //int xCoordinateOfTopRightSector = (int)Math.ceil(((center.n()+distance/2)-SwissBounds.MIN_N)/SECTOR_WIDTH);
            int yCoordinateOfBottomLeftSector = (int)Math.ceil(((4-distance/2)-0)/SECTOR_HEIGHT);
            //int identityOfTopRightSector= 128*(yCoordinateOfTopRightSector-1)+xCoordinateOfTopRightSector-1;

            //int xCoordinateOfTopLeftSector = (int)Math.ceil(((center.n()-distance/2)-SwissBounds.MIN_N)/SECTOR_WIDTH);
            int yCoordinateOfTopLeftSector = (int)Math.ceil(((4+distance/2)-0)/SECTOR_HEIGHT);
            //int identityOfTopLeftSector= 128*(yCoordinateOfTopLeftSector-1)+xCoordinateOfTopLeftSector-1;

/*        sectors.add(getSectorAtIdentity(identityOfBottomLeftSector));
        sectors.add(getSectorAtIdentity(identityOfBottomRightSector));
        sectors.add(getSectorAtIdentity(identityOfTopLeftSector));
        sectors.add(getSectorAtIdentity(identityOfTopRightSector));*/

            for(int x=xCoordinateOfBottomLeftSector; x<=xCoordinateOfBottomRightSector; ++x ){
                for(int y=yCoordinateOfBottomLeftSector; y<=yCoordinateOfTopLeftSector; ++y )
                    sectors.add(getSectorAtIdentity(NUMBER_OF_SECTORS_ON_SIDE*(y-1)+x-1));
            }
            return sectors;
        }

        /**
         * returns the sectors given its identity
         * @param identity the identity of the sector
         * @return the sector of given identity
         */
        public ch.epfl.javelo.data.GraphSectors.Sector getSectorAtIdentity(int identity) {
            //initiated startNodeId here to not calculate it twice
            int startNodeId=buffer.getInt(identity*SECTOR_BYTES+OFFSET_IDENTITY_OF_FIRST_NODE);
            return new ch.epfl.javelo.data.GraphSectors.Sector(startNodeId, startNodeId + buffer.getShort(identity*SECTOR_BYTES+OFFSET_NUMBER_OF_NODES));
        }

        /**
         * private inner record that represents a sector
         * @param startNodeId the index of the sector's first node
         * @param endNodeId the index of the node just after the sector's last node
         */
        public record Sector(int startNodeId, int endNodeId) {
        }


    @Test
    void sectorsInArea() {
        for(int i=0; i<64;++i) {
// Sens : inversé. Nœud destination : 12.
            buffer.putInt(6*i, i);
// Longueur : 0x10.b m (= 16.6875 m)
            buffer.putShort(6*i+4, (short)i);
        }
        List<GraphSectors.Sector> yeet= sectorsInArea(new PointCh(Ch1903.e(Math.toRadians(6.5790772), Math.toRadians(46.5218976)), Ch1903.n(Math.toRadians(6.5790772), Math.toRadians(46.5218976))), 3);

        int v =0;
        // Sens : inversé. Nœud destination : 12.
/*        buffer.putInt(0, 126);
// Longueur : 0x10.b m (= 16.6875 m)
        buffer.putShort(4, (short) 16);

        buffer.putInt(6, 66);
// Longueur : 0x10.b m (= 16.6875 m)
        buffer.putShort(6+4, (short) 20);

        GraphSectors sectors = new GraphSectors(buffer);
        sectors.getSectorAtIdentity(1);
        System.out.println(sectors.getSectorAtIdentity(1));*/
        ByteBuffer sectorsBuffer = ByteBuffer.allocate(16384*6);
        for(int i=0; i<16384;++i) {
// Sens : inversé. Nœud destination : 12.
            sectorsBuffer.putInt(6*i, i);
// Longueur : 0x10.b m (= 16.6875 m)
            sectorsBuffer.putShort(6*i+4, (short)i);
        }
        GraphSectors graph=new GraphSectors(sectorsBuffer);
        //graph.sectorsInArea(new PointCh(2600000, 1200000), 6000);
        List<GraphSectors.Sector> dondon= graph.sectorsInArea(new PointCh(2600000, 1200000), 600000000);
        int a =0;
        }
}