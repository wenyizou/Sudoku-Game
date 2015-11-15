package sudoku;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.TimerTask;
import java.util.Timer;

public class Sudoku {
	/**
	 * all component below
	 */
	private JFrame frame;
	Graphics g=null;
	private JTextField LegendRed;
	private MyPanel panel_ctrl1 = new MyPanel();
	private MyPanel panel_sudoku = new MyPanel();
	private MyTextField[] txtSudoku = new MyTextField[81];        // need to add final?
	private MyPanel panel_numleft = new MyPanel();
	private JMenuBar menuBar = new JMenuBar();
	private SudokuMethod sudoGame;
	private JTextField[] txtNumleft = new JTextField[81];
	private JPanel panel_numLeftText = new JPanel();
	private JTextField txtLeftAvailable;
	private JTextField txtNumber;
	private JRadioButtonMenuItem rdbtnmntmEazy;
	private JRadioButtonMenuItem rdbtnmntmMedium; 
	private JRadioButtonMenuItem rdbtnmntmHard;
	private JTextField textBlankBlockLeft;
	private JTextField txtBlankBlockLeft;
	private JTextField textTimerCount;
	
	//== below is timer ==//
	private Timer sudokuStopWatch = new Timer();
	private MyTimerTask sudokuTimeCount = new MyTimerTask();
	private JTextField LegendGreen;
	private JTextField txtTime;

	
	public class MyTextField extends JTextField implements FocusListener{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int txtPos;
		Character lastChar;
		
		MyTextField(int pos){
			this.txtPos=pos;                // used for store the position of the text field
			this.lastChar='.';
		}
		
//		public void actionPerformed(ActionEvent e){
//			i++;
//		}

		public void focusGained(FocusEvent e) {
			this.setForeground(Color.BLACK);
			String str=this.getText();
			if(str.length()==0) 
				this.lastChar='.';
			else
				this.lastChar=str.charAt(0);
			// TODO Auto-generated method stub
			
		}

		public void focusLost(FocusEvent e) {
			// TODO Auto-generated method stub
			String str=this.getText();
			Character now;
			if(str.length()==0 || str.length()>1) {
				now='.';
				sudoGame.updateSudokuBlock(txtPos, now, this);
				this.setText("");
				if(this.lastChar!='.')
					sudoGame.blankBlockLeft++;        // means reset a block to blank. stats +1
			}
			else{
				now=str.charAt(0);
				if(now<'1' || now>'9') {
					now='.';
					sudoGame.updateSudokuBlock(txtPos, now, this);
					this.setText("");
					if(this.lastChar!='.')
						sudoGame.blankBlockLeft++;        // means reset a block to blank. stats +1
				}
				else {
					sudoGame.updateSudokuBlock(txtPos, now, this);   // add this number to sudokuDisplay array
					sudoGame.checkValidSudoku(txtPos, now, txtSudoku);
					if(this.lastChar=='.')
						sudoGame.blankBlockLeft--;        // means set a block to filled. stats -1
				}
				
			}
			sudoGame.updateNumLeftBlock(lastChar, now, txtNumleft);
			//this.setForeground(Color.ORANGE);                 // indicate where were u last time
			textBlankBlockLeft.setText(sudoGame.blankBlockLeft.toString());       // update blank block left info
			// check if win
			if(sudoGame.blankBlockLeft==0)
				if(sudoGame.checkSudokuWin()){                // if win
					for(int i=0;i<81;i++){	
						txtSudoku[i].removeFocusListener(txtSudoku[i]);              // remove previous one if it has
					}
					JOptionPane.showMessageDialog(frame, "Congratz! You Win!");
				}
		}
		
	}
	
	
	public class MyTimerTask extends TimerTask{
		private Integer minutes=0;
		private Integer seconds=0;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			seconds++;
			if(seconds==60){
				seconds=0;
				minutes++;
			}
			if(minutes==60){
				sudokuStopWatch.cancel();
			}
			String min,sec;
			if(seconds<10)
				sec="0"+seconds.toString();
			else
				sec=seconds.toString();
			if(minutes<10)
				min="0"+minutes.toString();
			else
				min=minutes.toString();
			textTimerCount.setText(min+":"+sec);
		}
		
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Sudoku window = new Sudoku();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Sudoku() {
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 618, 488);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		initializeCtrlPane();
		initializeSudokuPane();
		initializeNumleftPane();
		initializeMenu();
		
		
		//g.drawLine(419, 2, 419, 400);
		//frame.getContentPane().add(panel);
	}
	
	private void initializeCtrlPane(){
		//==below is control panel==//
		
				panel_ctrl1.setBounds(390, 10, 212, 370);
				frame.getContentPane().add(panel_ctrl1);
				
				LegendRed = new JTextField();
				LegendRed.setEditable(false);
				LegendRed.setHorizontalAlignment(SwingConstants.CENTER);
				LegendRed.setText("Red : Invalid Number");
				LegendRed.setBounds(32, 234, 144, 32);
				panel_ctrl1.add(LegendRed);
				LegendRed.setBorder(new EmptyBorder(0,0,0,0));
				LegendRed.setForeground(Color.RED);
				LegendRed.setColumns(10);
				
				textBlankBlockLeft = new JTextField();
				textBlankBlockLeft.setText("81");
				textBlankBlockLeft.setEditable(false);
				textBlankBlockLeft.setBounds(138, 67, 25, 21);
				textBlankBlockLeft.setBorder(new EmptyBorder(0,0,0,0));
				panel_ctrl1.add(textBlankBlockLeft);
				textBlankBlockLeft.setColumns(10);
				
				txtBlankBlockLeft = new JTextField();
				txtBlankBlockLeft.setEditable(false);
				txtBlankBlockLeft.setText("Blank Block Left :");
				txtBlankBlockLeft.setBounds(27, 67, 114, 21);
				txtBlankBlockLeft.setBorder(new EmptyBorder(0,0,0,0));
				panel_ctrl1.add(txtBlankBlockLeft);
				txtBlankBlockLeft.setColumns(10);
				
				textTimerCount = new JTextField();
				textTimerCount.setEditable(false);
				textTimerCount.setBounds(99, 121, 45, 21);
				textTimerCount.setBorder(new EmptyBorder(0,0,0,0));
				panel_ctrl1.add(textTimerCount);
				textTimerCount.setColumns(10);
				
				LegendGreen = new JTextField();
				LegendGreen.setText("Green : Valid Number");
				LegendGreen.setHorizontalAlignment(SwingConstants.CENTER);
				LegendGreen.setForeground(Color.GREEN);
				LegendGreen.setEditable(false);
				LegendGreen.setColumns(10);
				LegendGreen.setBorder(new EmptyBorder(0,0,0,0));
				LegendGreen.setBounds(32, 276, 144, 32);
				panel_ctrl1.add(LegendGreen);
				
				txtTime = new JTextField();
				txtTime.setEditable(false);
				txtTime.setHorizontalAlignment(SwingConstants.CENTER);
				txtTime.setText("Time");
				txtTime.setBounds(42, 121, 55, 21);
				txtTime.setBorder(new EmptyBorder(0,0,0,0));
				panel_ctrl1.add(txtTime);
				txtTime.setColumns(10);
	}
	
	private void initializeSudokuPane(){
		//==below is sudoku zone==//
		
				panel_sudoku.setBounds(10, 10, 10+40*9, 10+40*9);
				frame.getContentPane().add(panel_sudoku);
				panel_sudoku.setLayout(new GridLayout(9,9));
				
				for(int i=0;i<81;i++){
					txtSudoku[i]=new MyTextField(i);
					txtSudoku[i].setHorizontalAlignment(SwingConstants.CENTER);
					txtSudoku[i].setFont(new Font("SimSun",Font.PLAIN,22));		
					//txtSudoku[i].addFocusListener(txtSudoku[i]);
					panel_sudoku.add(txtSudoku[i]);
				}
				
				//panel_sudoku.adda
				
				panel_sudoku.drawShape(ShapeKind.Line, 0, 122, 40*9+5, 122);     // draw 4 lines to separate the 3x3 zone
				panel_sudoku.drawShape(ShapeKind.Line, 0, 122*2+1, 40*9+5, 122*2+1);
				panel_sudoku.drawShape(ShapeKind.Line, 122, 0, 122, 40*9+5);
				panel_sudoku.drawShape(ShapeKind.Line, 122*2+1, 0, 122*2+1, 40*9+5);
	}
	 

	private void initializeNumleftPane(){
		//==below is number left zone===//
		
				panel_numleft.setBounds(133, 380, 40*9+10, 56);
				frame.getContentPane().add(panel_numleft);
				panel_numleft.setLayout(new GridLayout(2,9));
				
				panel_numLeftText.setBounds(10, 380, 111, 56);
				panel_numLeftText.setLayout(new GridLayout(2,1));
				frame.getContentPane().add(panel_numLeftText);
				
				txtNumber = new JTextField();
				txtNumber.setText("Number :");
				txtNumber.setHorizontalAlignment(SwingConstants.CENTER);
				txtNumber.setToolTipText("");
				txtNumber.setEditable(false);
				panel_numLeftText.add(txtNumber);
				txtNumber.setColumns(10);
				
				txtLeftAvailable = new JTextField();
				txtLeftAvailable.setText("Left Available :");
				txtLeftAvailable.setHorizontalAlignment(SwingConstants.CENTER);
				txtLeftAvailable.setEditable(false);
				panel_numLeftText.add(txtLeftAvailable);
				txtLeftAvailable.setColumns(10);
				for(int i=0;i<18;i++){
					txtNumleft[i]=new JTextField();
					txtNumleft[i].setEditable(false);
					txtNumleft[i].setHorizontalAlignment(SwingConstants.CENTER);
					Integer t=i%9+1;
					txtNumleft[t-1].setFont(new Font("Î¢ÈíÑÅºÚ", Font.BOLD, 14));
					txtNumleft[t-1].setText(t.toString());
					panel_numleft.add(txtNumleft[i]);
				}
	}

	private void initializeMenu(){
		//==========below is for menu==========//
		
		frame.setJMenuBar(menuBar);
		JMenu mnNewMenu = new JMenu("Game");
		mnNewMenu.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewGame = new JMenuItem("New Game");
		mntmNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int difficulty=1;
				if(rdbtnmntmEazy.isSelected())
					difficulty=1;
				if(rdbtnmntmMedium.isSelected())
					difficulty=2;
				if(rdbtnmntmHard.isSelected())
					difficulty=3;
				sudoGame = new SudokuMethod(txtSudoku,difficulty);
				for(int i=0;i<81;i++){	
					txtSudoku[i].removeFocusListener(txtSudoku[i]);              // remove previous one if it has
					txtSudoku[i].addFocusListener(txtSudoku[i]);
				}
				sudoGame.initNumLeftBlock(txtNumleft);
				textBlankBlockLeft.setText(sudoGame.blankBlockLeft.toString());       // update blank block left info
				sudokuTimeCount.seconds=0;
				sudokuTimeCount.minutes=0;
				if(sudokuTimeCount.scheduledExecutionTime()==0)
					sudokuStopWatch.scheduleAtFixedRate(sudokuTimeCount, 1000, 1000);     // repeat every 1 second
				panel_sudoku.paint(panel_sudoku.getGraphics());
			}
		});
	
		mntmNewGame.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));
		mnNewMenu.add(mntmNewGame);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		mntmExit.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));
		mnNewMenu.add(mntmExit);
		
		JMenu mnSetting = new JMenu("Settings");
		mnSetting.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));
		menuBar.add(mnSetting);
		
		JMenu mnDifficulty = new JMenu("Difficuly");
		mnDifficulty.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));
		mnSetting.add(mnDifficulty);
		
		rdbtnmntmEazy = new JRadioButtonMenuItem("Easy");
		rdbtnmntmEazy.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));
		rdbtnmntmEazy.setSelected(true);
		mnDifficulty.add(rdbtnmntmEazy);
		
		rdbtnmntmMedium = new JRadioButtonMenuItem("Medium");
		rdbtnmntmMedium.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));
		mnDifficulty.add(rdbtnmntmMedium);
		
		rdbtnmntmHard = new JRadioButtonMenuItem("Hard");
		rdbtnmntmHard.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));
		mnDifficulty.add(rdbtnmntmHard);
		
		ButtonGroup mnDifficultyGroup = new ButtonGroup();
		mnDifficultyGroup.add(rdbtnmntmEazy);
		mnDifficultyGroup.add(rdbtnmntmMedium);
		mnDifficultyGroup.add(rdbtnmntmHard);
		
		JMenu mnCheat = new JMenu("Cheat");
		mnCheat.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));
		menuBar.add(mnCheat);
		
		JMenuItem mntmShowAnswer = new JMenuItem("Show Answer($10)");
		mntmShowAnswer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(sudoGame!=null)
					sudoGame.showSudokuAnswer(txtSudoku, txtNumleft);
				else
					return;
			}
		});
		mntmShowAnswer.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));
		mnCheat.add(mntmShowAnswer);
	}
}
