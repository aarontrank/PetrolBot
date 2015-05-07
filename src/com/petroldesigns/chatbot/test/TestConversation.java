package com.petroldesigns.chatbot.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.petroldesigns.chatbot.conversation.ConversationManager;

public class TestConversation {
	public static void main(String[] args) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		ConversationManager cm = new ConversationManager(args[0]);
		String state = cm.getStartState();
		String response = "";
		do {
			System.out.println(cm.getText(state));
			response = in.readLine();
			state = cm.getNextState(state, response);
		} while (!cm.getText(state).equals(""));
	}
}
