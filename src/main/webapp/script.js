class MemoryCardGame {
    constructor() {
        this.gameBoard = document.getElementById('game-board');
        this.movesDisplay = document.getElementById('moves');
        this.newGameBtn = document.getElementById('new-game-btn');
        this.playAgainBtn = document.getElementById('play-again-btn');
        this.retryBtn = document.getElementById('retry-btn');
        this.winMessage = document.getElementById('win-message');
        this.loading = document.getElementById('loading');
        this.errorMessage = document.getElementById('error-message');
        this.errorText = document.getElementById('error-text');
        this.finalMoves = document.getElementById('final-moves');
        
        this.flippedCards = [];
        this.isProcessing = false;
        this.moves = 0;
        
        this.initEventListeners();
        this.startNewGame();
    }
    
    initEventListeners() {
        this.newGameBtn.addEventListener('click', () => this.startNewGame());
        this.playAgainBtn.addEventListener('click', () => {
            this.hideWinMessage();
            this.startNewGame();
        });
        this.retryBtn.addEventListener('click', () => {
            this.hideError();
            this.startNewGame();
        });
    }
    
    async startNewGame() {
        try {
            this.showLoading();
            this.resetGame();
            
            const response = await fetch('/game/start', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }
            });
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const data = await response.json();
            
            if (data.success) {
                this.createGameBoard(data.gridSize);
                this.updateMovesDisplay();
            } else {
                throw new Error(data.error || 'Failed to start game');
            }
        } catch (error) {
            console.error('Error starting game:', error);
            this.showError('Failed to start new game. Please try again.');
        } finally {
            this.hideLoading();
        }
    }
    
    resetGame() {
        this.flippedCards = [];
        this.isProcessing = false;
        this.moves = 0;
        this.gameBoard.innerHTML = '';
        this.hideWinMessage();
        this.hideError();
    }
    
    createGameBoard(gridSize) {
        this.gameBoard.innerHTML = '';
        
        for (let i = 0; i < gridSize; i++) {
            const card = document.createElement('div');
            card.className = 'card';
            card.dataset.position = i;
            card.addEventListener('click', () => this.handleCardClick(card, i));
            this.gameBoard.appendChild(card);
        }
    }
    
    async handleCardClick(cardElement, position) {
        // Prevent clicking if processing or card already flipped/matched
        if (this.isProcessing || 
            cardElement.classList.contains('flipped') || 
            cardElement.classList.contains('matched')) {
            return;
        }
        
        // Add card to flipped cards
        this.flippedCards.push({ element: cardElement, position: position });
        cardElement.classList.add('flipped');
        
        // If two cards are flipped, check for match
        if (this.flippedCards.length === 2) {
            this.isProcessing = true;
            await this.checkMatch();
        }
    }
    
    async checkMatch() {
        const [card1, card2] = this.flippedCards;
        
        try {
            const response = await fetch('/game/move', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    pos1: card1.position,
                    pos2: card2.position
                })
            });
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const data = await response.json();
            
            if (data.success) {
                // Display card values
                card1.element.textContent = this.getCardSymbol(data.card1);
                card2.element.textContent = this.getCardSymbol(data.card2);
                
                this.moves = data.moves;
                this.updateMovesDisplay();
                
                if (data.match) {
                    // Cards match - mark as matched
                    setTimeout(() => {
                        card1.element.classList.add('matched');
                        card2.element.classList.add('matched');
                        this.flippedCards = [];
                        this.isProcessing = false;
                        
                        // Check if game is won
                        if (data.gameWon) {
                            this.showWinMessage();
                        }
                    }, 500);
                } else {
                    // Cards don't match - flip back after 1 second
                    setTimeout(() => {
                        card1.element.classList.remove('flipped');
                        card2.element.classList.remove('flipped');
                        card1.element.textContent = '';
                        card2.element.textContent = '';
                        this.flippedCards = [];
                        this.isProcessing = false;
                    }, 1000);
                }
            } else {
                throw new Error(data.error || 'Failed to process move');
            }
        } catch (error) {
            console.error('Error checking match:', error);
            this.showError('Failed to process move. Please try again.');
            
            // Reset flipped cards on error
            card1.element.classList.remove('flipped');
            card2.element.classList.remove('flipped');
            card1.element.textContent = '';
            card2.element.textContent = '';
            this.flippedCards = [];
            this.isProcessing = false;
        }
    }
    
    getCardSymbol(cardValue) {
        const symbols = ['🎈', '🎯', '🎪', '🎨', '🎭', '🎪', '🎵', '🎸'];
        return symbols[cardValue - 1] || '❓';
    }
    
    updateMovesDisplay() {
        this.movesDisplay.textContent = this.moves;
    }
    
    showWinMessage() {
        this.finalMoves.textContent = this.moves;
        this.winMessage.classList.remove('hidden');
    }
    
    hideWinMessage() {
        this.winMessage.classList.add('hidden');
    }
    
    showLoading() {
        this.loading.classList.remove('hidden');
    }
    
    hideLoading() {
        this.loading.classList.add('hidden');
    }
    
    showError(message) {
        this.errorText.textContent = message;
        this.errorMessage.classList.remove('hidden');
    }
    
    hideError() {
        this.errorMessage.classList.add('hidden');
    }
}

// Initialize the game when the DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new MemoryCardGame();
});
