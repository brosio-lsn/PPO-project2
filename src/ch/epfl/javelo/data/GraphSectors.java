package ch.epfl.javelo.data;

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
        int xCoordinateOfBottomLeftSector = Math2.clamp(0, (int)Math.ceil(((center.e()-distance/2)-SwissBounds.MIN_E)/SECTOR_WIDTH), NUMBER_OF_SECTORS_ON_SIDE);
        int xCoordinateOfBottomRightSector = Math2.clamp(0, (int)Math.ceil(((center.e()+distance/2)-SwissBounds.MIN_E)/SECTOR_WIDTH), NUMBER_OF_SECTORS_ON_SIDE);;
        int yCoordinateOfBottomLeftSector = Math2.clamp(0, (int)Math.ceil(((center.n()-distance/2)-SwissBounds.MIN_N)/SECTOR_HEIGHT), NUMBER_OF_SECTORS_ON_SIDE);
        int yCoordinateOfTopLeftSector = Math2.clamp(0, (int)Math.ceil(((center.n()+distance/2)-SwissBounds.MIN_N)/SECTOR_HEIGHT), NUMBER_OF_SECTORS_ON_SIDE);
        int count=0;
        for(int y=yCoordinateOfBottomLeftSector; y<=yCoordinateOfTopLeftSector; ++y ) {
            for (int x = xCoordinateOfBottomLeftSector; x <= xCoordinateOfBottomRightSector; ++x) {
                //if (y > 0) yo.add(count);
                int b = NUMBER_OF_SECTORS_ON_SIDE * Math2.clamp(0, (y - 1), 127) + Math2.clamp(0, (x - 1), 127);
                if (y > 0)
                    sectors.add(getSectorAtIdentity(NUMBER_OF_SECTORS_ON_SIDE * Math2.clamp(0, (y - 1), 127) + Math2.clamp(0, (x - 1), 127)));
                if (y > 0)
                    //System.out.println(NUMBER_OF_SECTORS_ON_SIDE * Math2.clamp(0, (y - 1), 127) + Math2.clamp(0, (x - 1), 127));
                if (y > 0) ++count;
            }
        }
/*        System.out.println("NOMBRE DE yeet " + count);
        for(Integer i : yo){
            int yeet=0;
            System.out.println(i);
            for(Integer y : yo){
                if(y==i) ++yeet;
                if(yeet>1) System.out.print(y);
            }
        }*/
        ArrayList<Sector>se= new ArrayList<>();
        for(Sector s : sectors)se.add(s);
        for(Sector s : sectors) {
            System.out.println(s.startNodeId);
        }

        var sec2 = sectors.stream().distinct().toList();

        for(int i=0; i<sec2.size();++i) {
            int b = 0;
            for (int j = 0; j < se.size(); ++j){
                if(sec2.get(i).equals(se.get(j))&& b>0) {
                    System.out.println(se.get(j));
                }
                if(sec2.get(i).equals(se.get(j))&& b==0) {
                    ++b;
                }
            }
        }

        System.out.println("sdffffffffff");
        System.out.println(sec2.size());


        return sectors;
    }

    /**
     * returns the sectors given its identity
     * @param identity the identity of the sector
     * @return the sector of given identity
     */
    public Sector getSectorAtIdentity(int identity) {
        //initiated startNodeId here to not calculate it twice
        int startNodeId=buffer.getInt(identity*SECTOR_BYTES+OFFSET_IDENTITY_OF_FIRST_NODE);
        return new Sector(startNodeId, startNodeId + buffer.getShort(identity*SECTOR_BYTES+OFFSET_NUMBER_OF_NODES));
    }

    /**
     * private inner record that represents a sector
     * @param startNodeId the index of the sector's first node
     * @param endNodeId the index of the node just after the sector's last node
     */
    public record Sector(int startNodeId, int endNodeId) {
    }
}
