package a7;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TicTacToeWidget extends JPanel implements ActionListener, SpotListener {

	/* Enum to identify player. */
	
	private enum Player {WHITE, BLACK};
	
	private TicSpotBoard _board;		/* SpotBoard playing area. */
	private JLabel _message;		/* Label for messages. */
	private boolean _game_won;		/* Indicates if game was been won already.*/
	//private Spot _secret_spot;		/* Secret spot which wins the game. */
	//private Color _secret_spot_bg;  /* Needed to reset the background of the secret spot. */
	private Player _next_to_play;	/* Identifies who has next turn. */
	
	public TicTacToeWidget() {
		
		/* Create SpotBoard and message label. */
		
		_board = new TicSpotBoard(3,3);
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

		/* Reset the background of the old secret spot.
		 * Check _secret_spot for null first because call to 
		 * resetGame from constructor won't have a secret spot 
		 * chosen yet.
		 */
		
		/*if (_secret_spot != null) {
			_secret_spot.setBackground(_secret_spot_bg);
		}*/
		
		/* Pick a new secret spot. */
		
		/*int secret_x = (int) (Math.random() * _board.getSpotWidth());
		int secret_y = (int) (Math.random() * _board.getSpotWidth());
		_secret_spot = _board.getSpotAt(secret_x, secret_y);
		_secret_spot_bg = _secret_spot.getBackground();*/
		
		/* Reset game won and next to play fields */
		_game_won = false;
		_next_to_play = Player.WHITE;		
		
		/* Display game start message. */
		
		_message.setText("Welcome to the TicTacToe. WHITE to play");
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
	public void spotClicked(Spot s) {
		
		/* If game already won, do nothing. */
		if (_game_won) {
			return;
		}

		if (!s.isEmpty()) {
			return;
		}
		
		/* Set up player and next player name strings,
		 * and player color as local variables to
		 * be used later.
		 */
		
		String player_name = null;
		String next_player_name = null;
		Color player_color = null;
		
		if (_next_to_play == Player.WHITE) {
			player_color = Color.WHITE;
			player_name = "WHITE";
			next_player_name = "BLACK";
			_next_to_play = Player.BLACK;
		} else {
			player_color = Color.BLACK;
			player_name = "BLACK";
			next_player_name = "WHITE";
			_next_to_play = Player.WHITE;			
		}
				
		
		/* Set color of spot clicked and toggle. */
		s.setSpotColor(player_color);
		s.toggleSpot();

		/* Check if spot clicked is secret spot.
		 * If so, mark game as won and update background
		 * of spot to show that it was the secret spot.
		 */
		
		_game_won = checkGameWon(_board, s);
		/*if (_game_won) {
			s.setBackground(Color.RED);
		}*/

		int numSpotsFilled = 0;
		for(Spot iterSpot : _board)
		{
			if(!iterSpot.isEmpty())
			{
				numSpotsFilled+=1;
			}
		}
		if(numSpotsFilled == 9 && _game_won==false)
		{
			_message.setText("Spot at " + s.getCoordString() + " resulted in a draw.");
			return;
		}
		
		/* Update the message depending on what happened.
		 * If spot is empty, then we must have just cleared the spot. Update message accordingly.
		 * If spot is not empty and the game is won, we must have
		 * just won. Calculate score and display as part of game won message.
		 * If spot is not empty and the game is not won, update message to
		 * report spot coordinates and indicate whose turn is next.
		 */
		
		/*if (s.isEmpty()) {
			_message.setText(player_name + " clicked the spot at " + s.getCoordString() + ". " + next_player_name + " to play.");
		} else*/ {
			if (_game_won)  {
				//int score = _board.getSpotWidth() * _board.getSpotHeight();
				/*for (Spot board_spot : _board) {
					if (!board_spot.isEmpty()) {
						if (board_spot.getSpotColor() == player_color) {
							score -= 1;
						} else {
							score += 1;
						}
					}
				}*/
				_message.setText(player_name + " has won by filling the spot " + s.getCoordString() + ". " +
				                 /*"Score: " + score +*/ " Game over.");
			} else {
				_message.setText("Spot at " + s.getCoordString() + " is not a game winning move " + next_player_name + " to play.");
			}
		}
	}

	private boolean checkGameWon(TicSpotBoard _board2, Spot s) {
		
		int spotCount = 0;    //determines number of spots leading to winning
		
		//determines if game won vertically
		for(int i = 0; i < 3; i++) {
			int col = s.getSpotY();
			if(s.getSpotColor().equals(_board2.getSpotAt(i,col).getSpotColor()) && 
						!s.isEmpty()) {
				spotCount++;
			}
		}if(spotCount == 3) {
			return true;
		}
		
		spotCount = 0;
		//determines if game won horizontally
		for(int i = 0; i < 3; i++) {
			int row = s.getSpotX();
			if(s.getSpotColor().equals(_board2.getSpotAt(row, i).getSpotColor()) && 
						!s.isEmpty()) {
				spotCount++;
			}
		}if(spotCount == 3) {
			return true;
		}
		
		
		//determines if game won diagonally (bottom left to top right)
		spotCount = 0;
		for(int i = 0; i < 3; i++) {
			if(s.getSpotColor().equals(_board2.getSpotAt(i, 2-i).getSpotColor()) && 
						!s.isEmpty()) {
				spotCount++;
			}
		}if(spotCount == 3) {
			return true;
		}
			
		spotCount = 0;
		//determines if game won diagonally (top left to bottom right)
		for(int i = 0; i < 3; i++) {
			if(s.getSpotColor().equals(_board2.getSpotAt(i, i).getSpotColor()) && 
						!s.isEmpty()) {
				spotCount++;
			}
		}if(spotCount == 3) {
			return true;
		}
		return false;
	}

	@Override
	public void spotEntered(Spot s) {
		/* Highlight spot if game still going on. */
		
		if (_game_won) {
			return;
		}
		if(s.isEmpty()) {
			s.highlightSpot();
		}
	}

	@Override
	public void spotExited(Spot s) {
		/* Unhighlight spot. */
		
		s.unhighlightSpot();
	}
	
}