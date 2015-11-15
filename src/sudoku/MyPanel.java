package sudoku;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;


public class MyPanel extends JPanel {
	/**
	 * 
	 */
	// private shape class, use for store the shapes that have been drawed
	private class Shape{
		List<Integer> shapeFlag = new ArrayList<Integer>();
		List<List<Integer>> shapePara = new ArrayList<List<Integer>>();
	}
	
	
	private static final long serialVersionUID = 1L;
	
	Shape shape = new Shape();

	MyPanel(){
		this.setLayout(null);     // set as absolute layout
	}
	
	MyPanel(int x, int y, int w, int h){     // constructor with setBounds
		this.setLayout(null);
		this.setBounds(x, y, w, h);
	}
	
	public void paint(Graphics g){
		super.paint(g);
		//g.drawLine(110, 100, 220, 210);
		paintShape(shape,g);
	}
	
	private void paintShape(Shape shape, Graphics g){
		for(int i=0;i<shape.shapeFlag.size();i++){
			int s=shape.shapeFlag.get(i);
			List<Integer> para = shape.shapePara.get(i);
			int a1=para.get(0);
			int a2=para.get(1);
			int a3=para.get(2);
			int a4=para.get(3);
			switch(s){
			case 0:
				g.drawLine(a1, a2, a3, a4);break;
			case 1:
				g.drawRect(a1, a2, a3, a4);break;
			case 2:
				g.drawOval(a1, a2, a3, a4);break;
			}
		}
	}
	void drawShape(ShapeKind s, int a1, int a2, int a3, int a4){
		shape.shapeFlag.add(s.ordinal());
		List<Integer> para = new ArrayList<Integer>();
		para.add(a1);
		para.add(a2);
		para.add(a3);
		para.add(a4);
		shape.shapePara.add(para);
		paint(this.getGraphics());    // repaint
	}
	
	void clearShape(){
		shape.shapeFlag.clear();
		shape.shapePara.clear();
		paint(this.getGraphics());    // repaint
	}
	
	void withDraw(){        // same as ctrl+z
		if(shape.shapeFlag.size()==0) return;
		shape.shapeFlag.remove(shape.shapeFlag.size()-1);
		shape.shapePara.remove(shape.shapePara.size()-1);
		paint(this.getGraphics());    // repaint
	}
	
	
	
}
