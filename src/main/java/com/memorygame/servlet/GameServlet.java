package com.memorygame.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/game/*")
public class GameServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/start".equals(pathInfo)) {
                handleStart(request, response, out);
            } else if ("/move".equals(pathInfo)) {
                handleMove(request, response, out);
            } else if ("/score".equals(pathInfo)) {
                handleScore(request, response, out);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\": \"Endpoint not found\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }

    private void handleStart(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        HttpSession session = request.getSession();
        
        // Create card pairs (8 pairs = 16 cards)
        List<Integer> cards = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            cards.add(i);
            cards.add(i); // Add pair
        }
        
        // Shuffle cards
        Collections.shuffle(cards);
        
        // Store game state in session
        session.setAttribute("cards", cards);
        session.setAttribute("moves", 0);
        session.setAttribute("matchedPairs", 0);
        session.setAttribute("gameWon", false);
        
        // Create response with card layout (without revealing values)
        JsonObject response_obj = new JsonObject();
        response_obj.addProperty("success", true);
        response_obj.addProperty("gridSize", 16);
        response_obj.addProperty("message", "Game started successfully");
        
        out.write(gson.toJson(response_obj));
    }

    private void handleMove(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException {
        HttpSession session = request.getSession();
        
        // Parse request body
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        
        JsonObject requestData = gson.fromJson(sb.toString(), JsonObject.class);
        int pos1 = requestData.get("pos1").getAsInt();
        int pos2 = requestData.get("pos2").getAsInt();
        
        @SuppressWarnings("unchecked")
        List<Integer> cards = (List<Integer>) session.getAttribute("cards");
        Integer moves = (Integer) session.getAttribute("moves");
        Integer matchedPairs = (Integer) session.getAttribute("matchedPairs");
        
        if (cards == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Game not started\"}");
            return;
        }
        
        // Check if positions are valid
        if (pos1 < 0 || pos1 >= cards.size() || pos2 < 0 || pos2 >= cards.size() || pos1 == pos2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Invalid card positions\"}");
            return;
        }
        
        // Get card values
        int card1 = cards.get(pos1);
        int card2 = cards.get(pos2);
        
        // Check if cards match
        boolean match = (card1 == card2);
        
        // Increment moves
        moves++;
        session.setAttribute("moves", moves);
        
        // If match, increment matched pairs
        if (match) {
            matchedPairs++;
            session.setAttribute("matchedPairs", matchedPairs);
            
            // Check if game is won (all 8 pairs matched)
            if (matchedPairs == 8) {
                session.setAttribute("gameWon", true);
            }
        }
        
        // Create response
        JsonObject response_obj = new JsonObject();
        response_obj.addProperty("success", true);
        response_obj.addProperty("match", match);
        response_obj.addProperty("card1", card1);
        response_obj.addProperty("card2", card2);
        response_obj.addProperty("moves", moves);
        response_obj.addProperty("gameWon", matchedPairs == 8);
        
        out.write(gson.toJson(response_obj));
    }

    private void handleScore(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        HttpSession session = request.getSession();
        
        Integer moves = (Integer) session.getAttribute("moves");
        Integer matchedPairs = (Integer) session.getAttribute("matchedPairs");
        Boolean gameWon = (Boolean) session.getAttribute("gameWon");
        
        if (moves == null) {
            moves = 0;
        }
        if (matchedPairs == null) {
            matchedPairs = 0;
        }
        if (gameWon == null) {
            gameWon = false;
        }
        
        JsonObject response_obj = new JsonObject();
        response_obj.addProperty("success", true);
        response_obj.addProperty("moves", moves);
        response_obj.addProperty("matchedPairs", matchedPairs);
        response_obj.addProperty("gameWon", gameWon);
        
        out.write(gson.toJson(response_obj));
    }
}
