package com.contained.game.util;

public class ErrorCase {

	public enum Error {
		NONE, LEADER_ONLY, TEAM_ONLY, IND_ONLY, TEAM_FULL, NOT_EXISTS, INVALID, 
		ALREADY_LEADER, CANNOT_DEMOTE, ALREADY_OWNED, ADJACENT_ONLY, WRONG_TEAM
	};
	
	public static String getErrorCaption(ErrorCase.Error type) {
		switch(type) {
			case LEADER_ONLY:
				return "Only team leaders can perform this action.";
			case TEAM_ONLY:
				return "You must be in a team to perform this action.";
			case IND_ONLY:
				return "You can't perform this action because you're in a team.";
			case TEAM_FULL:
				return "The team is full, and can't accept more members.";
			case NOT_EXISTS:
				return "That doesn't exist.";
			case INVALID:
				return "Invalid command/argument.";
			default:
				return "";
		}
	}
	
}
