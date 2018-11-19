/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reversi;
import java.util.HashMap;
import java.util.Map;
import strategy.CachedMinMax.*;

/**
 *
 * @author MCL
 */
public class BoardViewTest {
    public static void main(String[] args) {
        Map<BoardView, Integer> cache = new HashMap<>();
        Map<Square, Player> cells1 = new HashMap<>();
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++) {
                if ((i+j)%2==0) {
                    cells1.put(new Square(i,j), Player.WHITE);                    
                }
                else {
                    cells1.put(new Square(i,j), Player.BLACK);
                }
            }
        }
        Map<Square, Player> cells2 = new HashMap<>();
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++) {
                if ((i+j)%2==0) {
                    cells2.put(new Square(i,j), Player.WHITE);                    
                }
                else {
                    cells2.put(new Square(i,j), Player.BLACK);
                }
            }
        }
        Map<Square, Player> cells3 = new HashMap<>();
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++) {
                if ((i+j)%2==0) {
                    cells3.put(new Square(i,j), Player.WHITE);                    
                }
                else {
                    cells3.put(new Square(i,j), Player.BLACK);
                }
            }
        }
        cells3.put(new Square(0,0), Player.BLACK);
        BoardView bv1 = new BoardView(cells1, Player.WHITE);
        BoardView bv2 = new BoardView(cells2, Player.WHITE);
        BoardView bv3 = new BoardView(cells3, Player.WHITE);
        
        cache.put(bv1, 30);
        cache.put(bv2, 40);
        cache.put(bv3, 50);
        System.out.println(cache.size());
        
        Map<Map<Square, Player>, Integer> m = new HashMap<>();
        m.put(cells1, 1);
        m.put(cells2, 2);
        m.put(cells3, 3);
        System.out.println(cells1.equals(cells2)?"1=2":"1!=2");
        System.out.println(cells2.equals(cells3)?"2=3":"2!=3");
        System.out.println(cells1.equals(cells3)?"1=3":"1!=3");
        System.out.println(m.size());
        
    }
    
}
