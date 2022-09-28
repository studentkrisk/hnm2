
import java.lang.Math;
import java.util.Scanner;
import java.util.ArrayList;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.StringBuilder;

public class hnm {

	public static double dist(int x1, int y1, int x2, int y2) {
		return Math.pow(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2), 0.5);
	}

	public static void massPrint(String s) {
		try {
			OutputStream out = new BufferedOutputStream ( System.out );
			out.write((s).getBytes());
		} catch (Exception e) {}
	}

	public static void main(String args[]) {

		Scanner sc = new Scanner(System.in);

		String[][] cmap = new String[32][32];

		ArrayList<Entity> ents = new ArrayList<Entity>();

		Hero h = new Hero(1, 1, 100);
		ents.add(new TownsPerson("msg1", "msg2", "msg3", new Item(), 1, 2, 2));
		ents.add(new Monster(8, 8));

		System.out.println(h.color + " ");
		
		for (int i = 0; i < cmap.length; i++) {
			for (int j = 0; j < cmap[0].length; j++) {
				if (i == 0 || j == 0 || i == cmap.length-1 || j == cmap[0].length-1) cmap[i][j] = Colors.BLACK;
				else cmap[i][j] = Colors.RESET;
			}
		}


		while (true) {
			String toPrint = "";
			for (int i = 0; i < cmap.length; i++) {
				for (int j = 0; j < cmap[0].length; j++) {
					if (j == h.x && i == h.y) toPrint += (h.color + "  " + Colors.RESET);
					else {
						if (dist(h.x, h.y, j, i) < 5.0) {
							boolean empty = true;
							for (Entity e : ents) {
								if (j == e.x && i == e.y) {
									toPrint += (e.color + "  " + Colors.RESET);
									empty = false;
									break;
								}
							} if (empty) toPrint += (cmap[i][j] + "  " + Colors.RESET);
						} else toPrint += (Colors.BLACK + "  " + Colors.RESET);
					}
				}
				toPrint += "\n";
			}

			massPrint(toPrint);

			System.out.print("Enter Input: ");
			String sinp = sc.nextLine();
			if (sinp.length() == 0) continue;
			char inp = sinp.charAt(0);
			System.out.print("\033[H\033[2J"); System.out.flush();
			switch(inp) {
				case 'w':
					h.move(cmap, -0, -1);
					break;
				case 'a':
					h.move(cmap, -1, -0);
					break;
				case 's':
					h.move(cmap, 0, 1);
					break;
				case 'd':
					h.move(cmap, 1, 0);
					break;
				case 'e':
					h.hinteract(ents);
					break;
				default:
					continue;
			}

			for (Entity e : ents) {
				e.update(h);
			}

		}
	}
}

class Entity {
	int x; int y;
	String color;
	char type;
	int priority;

	public void interact(Hero h) {}
	public void update(Hero h) {}

	public Entity (int sx, int sy, String c, char t, int p) {
		x = sx;
		y = sy;
		color = c;
		type = t;
		priority = p;
	}

	public String move(String[][] map, int dx, int dy) {
		x += dx;
		y += dy;
		if (map[y][x] == Colors.RESET) return null;
		x -= dx;
		y -= dy;
		return map[y+dy][x+dx];
	}

	public void moven(int dx, int dy) {
		x += dx;
		y += dy;
	}

	public double dist(int ox, int oy) {
		return Math.pow(Math.pow(x - ox, 2) + Math.pow(y - oy, 2), 0.5);
	}
}

class Hero extends Entity {
	int hp;
	int num = 0;
	ArrayList<Item> inv = new ArrayList<Item>();

	public Hero (int x, int y, int hp) {
		super(x, y, Colors.YELLOW, 'h', 0);
		hp = hp;
	}
	public boolean hinteract(ArrayList<Entity> ents) {
		Entity close = null;
		for (Entity e : ents) {
			if ((e.x == x && (e.y - 1 == y || e.y == y || e.y + 1 == y)) || (e.y == y && (e.x - 1 == x || e.x == x || e.x + 1 == x))) {
				if (close == null || e.priority > close.priority) close = e;
			}
		} if (close == null) return false;
		switch (close.type) {
			case 'p':
				break;
			case 't':
				close.interact(this);
				break;
			case 'm':
				break;
		}
		return true;
	}
}


class Monster extends Entity {
	int hp = 10;
	public Monster (int x, int y) {
		super(x, y, Colors.RED, 'm', 1);
	}

	public void update(Hero h) {
		if (dist(h.x, h.y) > 3.0) moven((int)Math.round(Math.random()*2-1), (int)Math.round(Math.random()*2-1));
		else {
			double[] dists = {
				h.dist(x+1, y),
				h.dist(x-1, y),
				h.dist(x, y+1),
				h.dist(x, y-1)
			};
		}
	}
}


class TownsPerson extends Entity {
	String msgi; // (initial) msg that plays when youre not done
	String msgd; // (done) msg that plays when you get the item
	String msgf; // (final) msg that plays after you got the item
	Item item;
	int num;
	char state = 'i';

	public TownsPerson (String i, String d, String f, Item t, int n, int x, int y) {
		super(x, y, Colors.BLUE, 't', 2);
		msgi = i;
		msgd = d;
		msgf = f;
		item = t;
		num = n;
	}

	public void interact(Hero h) {
		if (num <= h.num && state == 'i') state = 'd';
		switch (state) {
			case 'i':
				System.out.println(msgi);
				break;
			case 'd':
				System.out.println(msgd);
				h.inv.add(item);
				state = 'f';
				break;
			case 'f':
				System.out.println(msgf);
				break;
		}
	}
}

class Item {
}

class Colors {
	public static final String RESET = "\033[0m";

	public static final String BLACK = "\033[40m";  // BLACK
	public static final String RED = "\033[41m";    // RED
	public static final String GREEN = "\033[42m";  // GREEN
	public static final String YELLOW = "\033[43m"; // YELLOW
	public static final String BLUE = "\033[44m";   // BLUE
	public static final String PURPLE = "\033[45m"; // PURPLE
	public static final String CYAN = "\033[46m";   // CYAN
	public static final String WHITE = "\033[47m";  // WHITE

	public static final String BLACK_BRIGHT = "\033[0;100m";// BLACK
	public static final String RED_BRIGHT = "\033[0;101m";// RED
	public static final String GREEN_BRIGHT = "\033[0;102m";// GREEN
	public static final String YELLOW_BRIGHT = "\033[0;103m";// YELLOW
	public static final String BLUE_BRIGHT = "\033[0;104m";// BLUE
	public static final String PURPLE_BRIGHT = "\033[0;105m"; // PURPLE
	public static final String CYAN_BRIGHT = "\033[0;106m";  // CYAN
	public static final String WHITE_BRIGHT = "\033[0;107m";   // WHITE
}