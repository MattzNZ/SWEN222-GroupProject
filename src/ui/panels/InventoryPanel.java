package ui.panels;


import gameworld.Player;
import gameworld.entity.Item;
import gameworld.entity.armour.Armour;
import gameworld.entity.armour.ChainArmour;
import gameworld.entity.armour.LeatherArmour;
import gameworld.entity.armour.PlateArmour;
import gameworld.entity.armour.RobeArmour;
import gameworld.entity.weapon.ShankWeapon;
import gameworld.entity.weapon.SpearWeapon;
import gameworld.entity.weapon.Weapon;

import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;


public class InventoryPanel extends JLayeredPane implements MouseListener{

	Image backgroundImage; 
	ItemIcon[][] inventArray = new ItemIcon[4][2];
	InventoryBackground inventBackground = new InventoryBackground();
	private ItemIcon movedItem;
	private int movedItemIndex;
	private boolean lootOpen;
	private LootInventoryPanel lootInvent;
	private InventoryPanel self = this;
	private Player player;

	//Sound paths
	private String buttonSound = "src/ui/sounds/buttonSound.wav";

	public InventoryPanel(Player player){
		setLayout(null);
		setBounds(814, 637, 231, 262);
		this.player = player;

		//Add invent background
		this.add(inventBackground,0,0);

		//Add invent items
		populateInventArray();
		fillEquipmentSlots();
		addMouseListener(this);
	}

	public boolean addItem(ItemIcon item){
		for(int i = 0; i < inventArray[0].length; i++){
			for(int j = 0; j < inventArray.length; j++){
				if(inventArray[j][i].getName().equals("Empty")){
					inventArray[j][i] = item;
					System.out.println(inventArray[j][i].getName() + " Added");
					populateInvent();
					return true;
				}
				if(i == inventArray[0].length-1 && j == inventArray.length-1){
					if(inventArray[j][i] != null){
						System.out.println("Inventory is full");
						return false;
					}
				}
			}
		}
		return false;
	}

	public void populateInventArray(){	
		Item[] itemList = player.getInventory();

		System.out.println("----------------------");

		for(int i = 0; i < 4; i++){
			if(itemList[i] != null){
				inventArray[i][0] = new ItemIcon(itemList[i].getClass().getSimpleName(), itemList[i].getDescription());
				System.out.print(inventArray[i][0].getName() + ",");
			}
			else{
				inventArray[i][0] = null;
				System.out.print("Null,");
			}
		}

		System.out.println();

		for(int i = 0; i < 4; i++){
			if(itemList[i+4] != null){
				inventArray[i][1] = new ItemIcon(itemList[i+4].getClass().getSimpleName(), itemList[i+4].getDescription());
				System.out.print(inventArray[i][1].getName() + ",");
			}
			else{
				inventArray[i][1] = null;
				System.out.print("Null,");
			}
		}

		System.out.println();

		fillAllSlots();
	}

	/**
	 * Fills all slots with an empty item slot
	 */
	private void fillAllSlots(){
		System.out.println("-----------------");

		for(int j = 0; j < inventArray[0].length; j++){
			for(int i = 0; i < inventArray.length; i++){
				if(inventArray[i][j] == null){
					inventArray[i][j] = new ItemIcon("Empty", "Placeholder");
				}
			}
		}

		for(int j = 0; j < inventArray[0].length; j++){
			for(int i = 0; i < inventArray.length; i++){	
				System.out.print(inventArray[i][j].getName() + ",");
				if(i == 3){
					System.out.println();
				}
			}
		}
		System.out.println();
		System.out.println("-----------------");

		populateInvent();
	}

	/**
	 * Clears the inventory then populates it with elements of the inventory array
	 */
	private void populateInvent(){
		this.removeAll();

		this.add(inventBackground,0,0);

		for(int j = 0; j < inventArray[0].length; j++){
			for(int i = 0; i < inventArray.length; i++){
				if(inventArray[i][j] != null){
					inventArray[i][j].setX(11+(i*55));
					inventArray[i][j].setY(44+(j*70));
				}
			}
		}

		for(int j = 0; j < inventArray[0].length; j++){
			for(int i = 0; i < inventArray.length; i++){
				if(inventArray[i][j] != null){
					JLabel item = new JLabel(inventArray[i][j].getImage());
					item.setBounds(inventArray[i][j].getX(), inventArray[i][j].getY(), 42,52);
					this.add(item,1,0);
					if(!inventArray[i][j].getName().equals("Empty")){
						item.setToolTipText(inventArray[i][j].getDesciption());	
						item.addMouseListener(new MouseAdapter(){
							public void mouseClicked(MouseEvent e){
								self.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, self));
							}
							public void mousePressed(MouseEvent e){
								self.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, self));
							}
							public void mouseReleased(MouseEvent e){
								self.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, self));
							}
						});
					}
				}
			}
		}

		fillEquipmentSlots();
	}

	/**
	 * Swaps 2 elements of the array
	 * @param x1 - Item 1 array x coordinate
	 * @param y1 - Item 1 array y coordinate
	 * @param x2 - Item 2 array x coordinate
	 * @param y2 - Item 2 array y coordinate
	 */
	public void addItemTo(int x1, int y1, int x2, int y2){
		if(inventArray[x2][y2].equals(null)){
			inventArray[x2][y2] = inventArray[x1][y1];
			inventArray[x1][y1] = null;
		}
		else{
			ItemIcon temp = inventArray[x2][y2];
			inventArray[x2][y2] = inventArray[x1][y1];
			inventArray[x1][y1] = temp;
		}
		populateInventArray();
	}

	public void setLootVis(boolean change){
		this.lootOpen = change;
	}

	public void setLootInventPanel(LootInventoryPanel lootInvent){
		this.lootInvent = lootInvent;
	}

	private void fillEquipmentSlots(){
		if(player.getWeapon() != null){
			String name = player.getWeapon().getClass().getSimpleName();
			String desc = player.getWeapon().getDescription();
			ItemIcon weapon = new ItemIcon(name, desc);
			JLabel weaponLabel = new JLabel(weapon.getImage());
			weaponLabel.setBounds(65,195,42,52);
			this.add(weaponLabel,1,0);
		}
		if(player.getArmour() != null){
			String name = player.getArmour().getClass().getSimpleName();
			String desc = player.getArmour().getDescription();
			ItemIcon armour = new ItemIcon(name, desc);
			JLabel armourLabel = new JLabel(armour.getImage());
			armourLabel.setBounds(120,195,42,52);
			this.add(armourLabel,1,0);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {	

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	private int convertIndex(int i, int j){		
		if(j == 0){
			return i;
		}
		else{
			return i+4;
		}
	}

	private Item makeItem(String name){
		Item weapon = null;

		switch(name){
		case "ShankWeapon":
			weapon = new ShankWeapon("ShankWeapon", "Tis a shank mate", null, null);
			break;
		case "SpearWeapon":
			weapon = new SpearWeapon("SpearWeapon", "Tis a spear mate", null, null);
			break;
		case "ChainArmour":
			weapon = new ChainArmour("ChainArmour", "Tis sexy chain armour mate", null, null);
			break;
		case "LeatherArmour":
			weapon = new LeatherArmour("LeatherArmour", "Tis pretty shitty leather armour mate", null, null);
			break;
		case "PlateArmour":
			weapon = new PlateArmour("PlateArmour", "Tis super sexy plate armour m9", null, null);
			break;
		case "RobeArmour":
			weapon = new RobeArmour("RobeArmour", "Mate why even pick this shit up?", null, null);
			break;
		}

		return weapon;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		//Sets item to be moved in inventory
		if(e.getButton() == MouseEvent.BUTTON1){
			for(int i = 0; i < inventArray.length; i++){
				for(int j = 0; j < inventArray[0].length; j++){
					if(inventArray[i][j] != null && !inventArray[i][j].getName().equals("Empty")){
						if(inventArray[i][j].contains(e.getX(), e.getY())){
							movedItem = inventArray[i][j];
							movedItemIndex = convertIndex(i,j);
						}
					}
				}
			}
		}
		else if(e.getButton() == MouseEvent.BUTTON3){
			Item temp = null;
			//If right click on weapon slot
			if(e.getX() >= 65 && e.getX() <= 107 && e.getY() >= 195 && e.getY() <= 247){

				if(player.getWeapon() != null && player.addItem(player.getWeapon())){
					player.setWeapon(null);
					playSound("Button");
				}
				else{
					System.out.println("Inventory full can't dequip weapon");
				}
			}
			//If right click on armour slot
			else if(e.getX() >= 120 && e.getX() <= 162 && e.getY() >= 195 && e.getY() <= 247){
				if(player.getArmour() != null && player.addItem(player.getArmour())){
					player.setArmour(null);
					playSound("Button");
				}
				else{
					System.out.println("Inventory full can't dequip armour");
				}
			}
			else{
				//Check/Change items in invent
				for(int i = 0; i < inventArray.length; i++){
					for(int j = 0; j < inventArray[0].length; j++){
						if(inventArray[i][j]!= null && inventArray[i][j].getName() != "Empty"){
							if(inventArray[i][j].contains(e.getX(), e.getY())){
								if(inventArray[i][j].getType().equals("Weapon")){
									if(player.getWeapon() != null){
										temp = player.getWeapon();
									}
									Weapon newWeapon = (Weapon) makeItem(inventArray[i][j].getName());
									player.setWeapon(newWeapon);											
									player.removeItem(convertIndex(i,j));
									
									if(temp != null){
										player.addItem(temp);
									}
									playSound("Button");
								}
								else if(inventArray[i][j].getType().equals("Armour")){
									if(player.getArmour() != null){
										temp = player.getArmour();
									}
									Armour newArmour = (Armour) makeItem(inventArray[i][j].getName());
									player.setArmour(newArmour);
									player.removeItem(convertIndex(i,j));
									
									if(temp != null){
										player.addItem(temp);
									}
									playSound("Button");
								}
							}
						}
					}
				}
			}
		}
		populateInventArray();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			if(movedItem != null){
				for(int i = 0; i < inventArray.length; i++){
					for(int j = 0; j < inventArray[0].length; j++){
						if(inventArray[i][j]!= null){
							if(inventArray[i][j].contains(e.getX(), e.getY()) && !inventArray[i][j].getName().equals(movedItem.getName())){
								player.swapItems(movedItemIndex, convertIndex(i,j));
								movedItem = null;
								playSound("Button");
							}
						}
					}
				}
				if(!lootOpen){
					if(e.getX() < -2 || e.getY() < -3){
						System.out.println("Dropped");
					}
				}
				else{
					System.out.println(e.getX() + " " + e.getY());
					if(e.getX() > -454 && e.getX() < -130 && e.getY() > -521 && e.getY() < -319){
						System.out.println("Inside");
						for(int i = 0; i < inventArray.length; i++){
							for(int j = 0; j < inventArray[0].length; j++){
								if(inventArray[i][j]!= null && inventArray[i][j].getName() != "Empty"){
									if(inventArray[i][j].contains(movedItem.getX(), movedItem.getY())){
										if(lootInvent.addItem(movedItem)){
											inventArray[i][j] = new ItemIcon("Empty", "Placeholder");
											playSound("Button");
										}
										else{
											System.out.println("Loot inventory is full can't swap item");
										}	
									}
								}
							}
						}
					}
				}
			}
			populateInventArray();
		}
	}


	private void playSound(String sound){
		String soundPath = null;
		switch(sound){
		case "Button":
			soundPath = buttonSound;
			break;
		default:
			break;
		}
		try{
			File file = new File(soundPath);
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(file));
			clip.start();
		}catch(Exception e){
			System.out.println(e.getLocalizedMessage());
		}
	}
}
