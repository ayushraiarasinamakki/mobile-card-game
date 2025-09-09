# Memory Card Game

A simple memory card matching game built with Java servlets backend and HTML/CSS/JavaScript frontend.

## Features

- **Interactive Game Board**: 4x4 grid of cards (8 pairs to match)
- **Card Matching Logic**: Click two cards to reveal them, matching pairs stay revealed
- **Move Counter**: Tracks the number of moves made
- **Win Detection**: Shows congratulations message when all pairs are matched
- **Responsive Design**: Works on desktop and mobile devices
- **Clean UI**: Modern gradient design with smooth animations
- **Session Management**: Game state stored in server session

## Technology Stack

### Backend
- **Java Servlets**: RESTful API endpoints
- **Maven**: Build and dependency management
- **Gson**: JSON processing
- **HTTP Sessions**: Game state management

### Frontend
- **HTML5**: Semantic markup
- **CSS3**: Modern styling with gradients and animations
- **JavaScript (ES6)**: Game logic and API communication
- **Responsive Design**: Mobile-friendly layout

## API Endpoints

### POST /game/start
Initializes a new game session.
- **Response**: `{"success": true, "gridSize": 16, "message": "Game started successfully"}`

### POST /game/move
Processes a move (two card positions).
- **Request**: `{"pos1": 0, "pos2": 5}`
- **Response**: `{"success": true, "match": true, "card1": 3, "card2": 3, "moves": 5, "gameWon": false}`

### GET /game/score
Returns current game statistics.
- **Response**: `{"success": true, "moves": 5, "matchedPairs": 2, "gameWon": false}`

## Project Structure

```
Memory card game/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/memorygame/servlet/
│       │       └── GameServlet.java
│       └── webapp/
│           ├── WEB-INF/
│           │   └── web.xml
│           ├── index.html
│           ├── style.css
│           └── script.js
├── pom.xml
└── README.md
```

## Setup Instructions

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher
- Web server (Tomcat 9+ or Jetty)

### Build and Run

1. **Clone/Download the project**
   ```bash
   cd "d:\trae projects\Memory card game"
   ```

2. **Build the project**
   ```bash
   mvn clean compile
   ```

3. **Run with Jetty (Recommended)**
   ```bash
   mvn jetty:run
   ```
   The application will be available at: `http://localhost:8080/memory-game`

4. **Alternative: Run with Tomcat**
   ```bash
   mvn tomcat7:run
   ```
   The application will be available at: `http://localhost:8080/memory-game`

5. **Build WAR file for deployment**
   ```bash
   mvn clean package
   ```
   The WAR file will be created in the `target/` directory.

### Manual Deployment

1. Build the WAR file: `mvn clean package`
2. Copy `target/memory-card-game.war` to your Tomcat `webapps/` directory
3. Start Tomcat
4. Access the game at: `http://localhost:8080/memory-card-game`

## How to Play

1. **Start the Game**: Click "New Game" to shuffle and start a new game
2. **Flip Cards**: Click on any face-down card to reveal it
3. **Match Pairs**: Click a second card to see if they match
   - If they match: Both cards stay revealed
   - If they don't match: Both cards flip back after 1 second
4. **Win Condition**: Match all 8 pairs to win the game
5. **Track Progress**: Your move count is displayed at the top

## Game Rules

- The game uses 8 different card symbols (🎈, 🎯, 🎪, 🎨, 🎭, 🎵, 🎸, etc.)
- Each symbol appears exactly twice on the board
- Cards are randomly shuffled at the start of each game
- Only two cards can be flipped at a time
- Matched pairs cannot be clicked again
- The goal is to match all pairs in the fewest moves possible

## Development

### Adding New Features
- **More Card Pairs**: Modify the loop in `GameServlet.handleStart()` to create more pairs
- **Different Grid Sizes**: Update the CSS grid template in `style.css`
- **New Card Symbols**: Add more symbols to the `getCardSymbol()` function in `script.js`
- **Difficulty Levels**: Implement different grid sizes and pair counts

### Troubleshooting
- **Port Conflicts**: Change the port in `pom.xml` if 8080 is already in use
- **Build Issues**: Ensure Java 11+ and Maven 3.6+ are installed
- **Session Issues**: Clear browser cookies if game state becomes corrupted

## Browser Compatibility
- Chrome 60+
- Firefox 55+
- Safari 12+
- Edge 79+

## License
This project is open source and available under the MIT License.
