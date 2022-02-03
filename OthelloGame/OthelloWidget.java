package a7;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class OthelloWidget extends JPanel implements ActionListener, SpotListener {

	/* Enum to identify player. */
	
	private enum Player {BLACK, WHITE};
	
	private OthelloSpotBoard _board;		/* SpotBoard playing area. */
	private JLabel _message;		/* Label for messages. */
	private boolean _game_won;		/* Indicates if games was been won already.*/
	//private Spot _secret_spot;		/* Secret spot which wins the game. */
	//private Color _secret_spot_bg;  /* Needed to reset the background of the secret spot. */
	private Player _next_to_play;	/* Identifies who has next turn. */
	private Color _next_to_play_color; /* Identifies color of next player. */
	
	public OthelloWidget() {
		
		/* Create SpotBoard and message label. */
		
		_board = new OthelloSpotBoard(8,8);
		_message = new JLabel();
		
		/* Set layout and place SpotBoard at center. */
		
		setLayout(new BorderLayout());
		add(_board, BorderLayout.CENTER);

		/* Create subpanel for message area and reset button. */
		
		JPanel reset_message_panel = new JPanel();
		reset_message_panel.setLayout(new BorderLayout());

		/* Reset button. Add ourselves as the action listener. */
		
		JButton reset_button = new JButton("Restart");
		reset_button.addActionListener(this);
		reset_message_panel.add(reset_button, BorderLayout.EAST);
		reset_message_panel.add(_message, BorderLayout.CENTER);

		/* Add subpanel in south area of layout. */
		
		add(reset_message_panel, BorderLayout.SOUTH);

		/* Add ourselves as a spot listener for all of the
		 * spots on the spot board.
		 */
		_board.addSpotListener(this);

		/* Reset game. */
		resetGame();
	}

	/* resetGame
	 * 
	 * Resets the game by clearing all the spots on the board,
	 * picking a new secret spot, resetting game status fields, 
	 * and displaying start message.
	 * 
	 */

	private void resetGame() {
		/* Clear all spots on board. Uses the fact that SpotBoard
		 * implements Iterable<Spot> to do this in a for-each loop.
		 */

		for (Spot s : _board) {
			s.clearSpot();
			s.setSpotColor(Color.YELLOW);
		}
		
		/* Reset game won and next to play fields */
		_game_won = false;
		_next_to_play = Player.BLACK;
		_next_to_play_color = Color.BLACK;
		
		_board.getSpotAt(3, 3).setSpotColor(Color.WHITE);
		_board.getSpotAt(3, 3).toggleSpot();
		
		_board.getSpotAt(4, 4).setSpotColor(Color.WHITE);
		_board.getSpotAt(4, 4).toggleSpot();

		_board.getSpotAt(4, 3).setSpotColor(Color.BLACK);
		_board.getSpotAt(4, 3).toggleSpot();
		
		_board.getSpotAt(3, 4).setSpotColor(Color.BLACK);
		_board.getSpotAt(3, 4).toggleSpot();
		
		/* Display game start message. */
		
		_message.setText("Welcome to the Othello. BLACK to play");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		/* Handles reset game button. Simply reset the game. */
				resetGame();
	}

	/* Implementation of SpotListener below. Implements game
	 * logic as responses to enter/exit/click on spots.
	 */
	
	@Override
	public void spotEntered(Spot s) {
		/* Highlight spot if game still going on. */
		
		if (_game_won) {
			return;
		}
		if(s.isEmpty() && isSpotGood(s)) {
			s.highlightSpot();
		}
		return;
	}
	
	@Override
	public void spotExited(Spot s) {
		/* Unhighlight spot. */
		s.unhighlightSpot();
	}
	
	@Override
	public void spotClicked(Spot s) {
		
		/* If game already won, do nothing. */
		if (_game_won) {
			return;
		}
		
		//if valid spot
		if(!isSpotGood(s)) {
			return;
		}
		/* Set color of spot clicked and toggle. */
			s.setSpotColor(_next_to_play_color);
			s.setSpot();
			List<Spot> flipSpots = getFlippedSpots(s, _board);
			for(Spot spoot: flipSpots) {
				spoot.setSpotColor(_next_to_play_color);
				spoot.setSpot();
			}
		
		/* Set up player and next player name strings,
		 * and player color as local variables to
		 * be used later.
		 */
		
		String next_player_name = null;
		
		//if all spots are filled game won is true
		_game_won = checkGameWon(_board, s);
		
		/* Update the message depending on what happened.
		 * If spot is empty, then we must have just cleared the spot. Update message accordingly.
		 * If spot is not empty and the game is won, we must have
		 * just won. Calculate score and display as part of game won message.
		 * If spot is not empty and the game is not won, update message to
		 * report spot coordinates and indicate whose turn is next.
		 */
		
		if (_game_won)  {
			gameWon();
			} else {
				next_player_name = nextToPlay();
				if(next_player_name != null) {
					_message.setText(next_player_name + " to play.");
				}else {
					gameWon();
				}
		}
	}

	private void switchPlayer() {
		if (_next_to_play == Player.BLACK) {
			_next_to_play_color = Color.WHITE;
			_next_to_play = Player.WHITE;
		} else {
			_next_to_play_color = Color.BLACK;
			_next_to_play = Player.BLACK;			
		}
	}

	private boolean isSpotGood(Spot s) {
		if (s.isEmpty() && getFlippedSpots(s, _board) != null) {
			return true;
		}return false;
	}

	private boolean checkGameWon(OthelloSpotBoard _board2, Spot s) {
		
		for(Spot spoot: _board2) {
			if(spoot.isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	private void gameWon() {
		int blackScore = 0;
		int whiteScore = 0;
		for(Spot spoot : _board) {
			if(spoot.getSpotColor() == Color.BLACK) {
				blackScore++;
			}else {
				whiteScore++;
			}
		}
		if(whiteScore != blackScore) {
			_message.setText( "Game Over. " + ((whiteScore > blackScore) ? "WHITE" : "BLACK") + 
					" wins. Score: " + ((whiteScore > blackScore) ? whiteScore + ":" + blackScore
							:blackScore + ":" + whiteScore));
		}else {
			_message.setText( "Game Over. It's a Draw.");
		}
	}
	private String nextToPlay() {
		switchPlayer();
		for(Spot spoot : _board) {
			if(isSpotGood(spoot)) {
				if(_next_to_play_color == Color.BLACK) {
					return "BLACK";
				}else {
					return "WHITE";
				}
			}
		}
		switchPlayer();
		for(Spot spoot : _board) {
			if(isSpotGood(spoot)) {
				if(_next_to_play_color == Color.BLACK) {
					return "BLACK"; 
				}else {
					return "WHITE";
				}
			}
		}
		return null;
	}

	private List<Spot> getFlippedSpots(Spot s, OthelloSpotBoard _board2) {
		List<Spot> flipSpots = new ArrayList<Spot>();
		
		//check to see if spots above can be flipped
		if(canFlipSpotsAbove(s)) {
			Spot temp = _board2.getSpotAt(s.getSpotX(), s.getSpotY() - 1);
			//run until spot above it is same color as s
			while(temp.getSpotColor() != _next_to_play_color) {
				flipSpots.add(temp);
				temp = _board2.getSpotAt(temp.getSpotX(), temp.getSpotY() - 1);
			}
		}
		
		//check to see if spots below can be flipped
		if(canFlipSpotsBelow(s)) {
			Spot temp = _board2.getSpotAt(s.getSpotX(), s.getSpotY() + 1);
			//run until spot above it is same color as s
			while(temp.getSpotColor() != _next_to_play_color) {
				flipSpots.add(temp);
				temp = _board2.getSpotAt(temp.getSpotX(), temp.getSpotY() + 1);
			}
		}
		
		//check to see if spots to the left can be flipped
		if(canFlipSpotsLeft(s)) {
			Spot temp = _board2.getSpotAt(s.getSpotX() - 1, s.getSpotY());
			//run until spot above it is same color as s
			while(temp.getSpotColor() != _next_to_play_color) {
				flipSpots.add(temp);
				temp = _board2.getSpotAt(temp.getSpotX() - 1, temp.getSpotY());
			}
		}
		
		//check to see if spots to the right can be flipped
		if(canFlipSpotsRight(s)) {
			Spot temp = _board2.getSpotAt(s.getSpotX() + 1, s.getSpotY());
			//run until spot above it is same color as s
			while(temp.getSpotColor() != _next_to_play_color) {
				flipSpots.add(temp);
				temp = _board2.getSpotAt(temp.getSpotX() + 1, temp.getSpotY());
			}
		}
		//check to see if spots up and to the right can be flipped
		if(canFlipSpotsUpRight(s)) {
			Spot temp = _board2.getSpotAt(s.getSpotX() + 1, s.getSpotY() - 1);
			//run until spot above it is same color as s
			while(temp.getSpotColor() != _next_to_play_color) {
				flipSpots.add(temp);
				temp = _board2.getSpotAt(temp.getSpotX() + 1, temp.getSpotY() - 1);
			}
		}
		
		//check to see if spots up and to the right can be flipped
		if(canFlipSpotsUpLeft(s)) {
			Spot temp = _board2.getSpotAt(s.getSpotX() - 1, s.getSpotY() - 1);
			//run until spot above it is same color as s
			while(temp.getSpotColor() != _next_to_play_color) {
				flipSpots.add(temp);
				temp = _board2.getSpotAt(temp.getSpotX() - 1, temp.getSpotY() - 1);
			}
		}
		
		//check to see if spots up and to the right can be flipped
		if(canFlipSpotsDownRight(s)) {
			Spot temp = _board2.getSpotAt(s.getSpotX() + 1, s.getSpotY() + 1);
			//run until spot above it is same color as s
			while(temp.getSpotColor() != _next_to_play_color) {
				flipSpots.add(temp);
				temp = _board2.getSpotAt(temp.getSpotX() + 1, temp.getSpotY() + 1);
			}
		}
		
		//check to see if spots up and to the right can be flipped
		if(canFlipSpotsDownLeft(s)) {
			Spot temp = _board2.getSpotAt(s.getSpotX() - 1, s.getSpotY() + 1);
			//run until spot above it is same color as s
			while(temp.getSpotColor() != _next_to_play_color) {
				flipSpots.add(temp);
				temp = _board2.getSpotAt(temp.getSpotX() - 1, temp.getSpotY() + 1);
			}
		}
		
		//if no spots were found, return null to indicate problem
		if(flipSpots.size() == 0) {
			return null;
		}
		return flipSpots;
	}

	private boolean canFlipSpotsAbove(Spot s) {
		Spot temp;
		//check to see if there's a spot above it, and if it's a different color
		if(s.getSpotY() != 0) {
			temp = _board.getSpotAt(s.getSpotX(), s.getSpotY() - 1);
			//if spot above is empty or the same color as spot return false
			if(temp.isEmpty() || temp.getSpotColor() == _next_to_play_color) {
				return false;
			}
		}else {
			return false;
		}
		//
		while(temp.getSpotY() > 0 && !temp.isEmpty()) {
			temp = _board.getSpotAt(temp.getSpotX(), temp.getSpotY() - 1);
			if(temp.getSpotColor().equals(_next_to_play_color)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean canFlipSpotsBelow(Spot s) {
		Spot temp;
		//check to see if there's a spot above it, and if it's a different color
		if(s.getSpotY() < _board.getSpotHeight() - 1) {
			temp = _board.getSpotAt(s.getSpotX(), s.getSpotY() + 1);
			//if spot above is empty or the same color as spot return false
			if(temp.isEmpty() || temp.getSpotColor() == _next_to_play_color) {
				return false;
			}
		}else {
			return false;
		}
		//
		while(temp.getSpotY() < _board.getSpotHeight() - 1 && !temp.isEmpty()) {
			temp = _board.getSpotAt(temp.getSpotX(), temp.getSpotY() + 1);
			if(temp.getSpotColor().equals(_next_to_play_color)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean canFlipSpotsLeft(Spot s) {
		Spot temp;
		//check to see if there's a spot above it, and if it's a different color
		if(s.getSpotX() > 0 ) {
			temp = _board.getSpotAt(s.getSpotX() - 1, s.getSpotY());
			//if spot above is empty or the same color as spot return false
			if(temp.isEmpty() || temp.getSpotColor() == _next_to_play_color) {
				return false;
			}
		}else {
			return false;
		}
		//
		while(temp.getSpotX() > 0 && !temp.isEmpty()) {
			temp = _board.getSpotAt(temp.getSpotX() - 1, temp.getSpotY());
			if(temp.getSpotColor().equals(_next_to_play_color)) {
				return true;
			}
		}
		return false;
	}

	private boolean canFlipSpotsRight(Spot s) {
		Spot temp;
		//check to see if there's a spot above it, and if it's a different color
		if(s.getSpotX() < _board.getSpotWidth() - 1 ) {
			temp = _board.getSpotAt(s.getSpotX() + 1, s.getSpotY());
			//if spot above is empty or the same color as spot return false
			if(temp.isEmpty() || temp.getSpotColor() == _next_to_play_color) {
				return false;
			}
		}else {
			return false;
		}
		//
		while(temp.getSpotX() < _board.getSpotWidth() - 1 && !temp.isEmpty()) {
			temp = _board.getSpotAt(temp.getSpotX() + 1, temp.getSpotY());
			if(temp.getSpotColor().equals(_next_to_play_color)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean canFlipSpotsUpRight(Spot s) {
		Spot temp;
		//check to see if there's a spot above it, and if it's a different color
		if(s.getSpotX() < _board.getSpotWidth() - 1 && s.getSpotY() > 0) {
			temp = _board.getSpotAt(s.getSpotX() + 1, s.getSpotY() - 1);
			//if spot above is empty or the same color as spot return false
			if(temp.isEmpty() || temp.getSpotColor() == _next_to_play_color) {
				return false;
			}
		}else {
			return false;
		}
		//
		while(temp.getSpotX() < _board.getSpotWidth() - 1 && temp.getSpotY() > 0 && !temp.isEmpty()) {
			temp = _board.getSpotAt(temp.getSpotX() + 1, temp.getSpotY() - 1);
			if(temp.getSpotColor().equals(_next_to_play_color)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean canFlipSpotsUpLeft(Spot s) {
		Spot temp;
		//check to see if there's a spot above it, and if it's a different color
		if(s.getSpotX() > 0 && s.getSpotY() > 0) {
			temp = _board.getSpotAt(s.getSpotX() - 1, s.getSpotY() - 1);
			//if spot above is empty or the same color as spot return false
			if(temp.isEmpty() || temp.getSpotColor() == _next_to_play_color) {
				return false;
			}
		}else {
			return false;
		}
		//
		while(temp.getSpotX() > 0 && temp.getSpotY() > 0 && !temp.isEmpty()) {
			temp = _board.getSpotAt(temp.getSpotX() - 1, temp.getSpotY() - 1);
			if(temp.getSpotColor().equals(_next_to_play_color)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean canFlipSpotsDownRight(Spot s) {
		Spot temp;
		//check to see if there's a spot above it, and if it's a different color
		if(s.getSpotX() < _board.getSpotWidth() - 1 && s.getSpotY() < _board.getSpotHeight() - 1) {
			temp = _board.getSpotAt(s.getSpotX() + 1, s.getSpotY() + 1);
			//if spot above is empty or the same color as spot return false
			if(temp.isEmpty() || temp.getSpotColor() == _next_to_play_color) {
				return false;
			}
		}else {
			return false;
		}
		//
		while(temp.getSpotX() < _board.getSpotWidth() - 1 && temp.getSpotY() < 
				_board.getSpotHeight() - 1 && !temp.isEmpty()) {
			
			temp = _board.getSpotAt(temp.getSpotX() + 1, temp.getSpotY() + 1);
			if(temp.getSpotColor().equals(_next_to_play_color)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean canFlipSpotsDownLeft(Spot s) {
		Spot temp;
		//check to see if there's a spot above it, and if it's a different color
		if(s.getSpotX() > 0 && s.getSpotY() < _board.getSpotHeight() - 1) {
			temp = _board.getSpotAt(s.getSpotX() - 1, s.getSpotY() + 1);
			//if spot above is empty or the same color as spot return false
			if(temp.isEmpty() || temp.getSpotColor() == _next_to_play_color) {
				return false;
			}
		}else {
			return false;
		}
		//
		while(temp.getSpotX() > 0 && temp.getSpotY() < _board.getSpotHeight() - 1 && !temp.isEmpty()) {
			temp = _board.getSpotAt(temp.getSpotX() - 1, temp.getSpotY() + 1);
			if(temp.getSpotColor().equals(_next_to_play_color)) {
				return true;
			}
		}
		return false;
	}
}