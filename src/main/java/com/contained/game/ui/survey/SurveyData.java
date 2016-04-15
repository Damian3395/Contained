package com.contained.game.ui.survey;

import net.minecraft.nbt.NBTTagCompound;

/**
 * http://ipip.ori.org/BFASKeys.htm
 * http://ipip.ori.org/New_IPIP-50-item-scale.htm
 */
public class SurveyData {

	public static final Q[] data = {
		new Q("I respect authority.", Q.AGREEABLENESS, 1),
		new Q("I avoid philosophical discussions.", Q.OPENNESS, -1),
		new Q("I need a creative outlet.", Q.OPENNESS, 1),
		new Q("I mainly work for my own personal gain.", Q.AGREEABLENESS, -1),
		new Q("I don't get embarassed easily.", Q.NEUROTICISM, -1),
		new Q("I enjoy the beauty of nature.", Q.OPENNESS, 1),
		new Q("I take an interest in others.", Q.AGREEABLENESS, 1),
		new Q("I often worry about things.", Q.NEUROTICISM, 1),
		new Q("I get things done quickly.", Q.CONSCIENTIOUSNESS, 1),
		new Q("I can handle a lot of information.", Q.OPENNESS, 1),
		new Q("I am the first to act.", Q.EXTRAVERSION, 1),
		new Q("I dislike routine.", Q.CONSCIENTIOUSNESS, -1),
		new Q("I see myself as a good leader.", Q.EXTRAVERSION, 1),
		new Q("I love to reflect on things.", Q.OPENNESS, 1),
		new Q("I keep a clean workspace.", Q.CONSCIENTIOUSNESS, 1),
		new Q("I am relaxed most of the time.", Q.NEUROTICISM, -1),
		new Q("I feel comfortable around people.", Q.EXTRAVERSION, 1),
		new Q("I feel comfortable with myself.", Q.NEUROTICISM, -1),
		new Q("I tend to keep to myself.", Q.EXTRAVERSION, -1),
		new Q("I experience frequent mood swings.", Q.NEUROTICISM, 1),
		new Q("I follow a schedule.", Q.CONSCIENTIOUSNESS, 1),
		new Q("I'm not interested in other's problems.", Q.AGREEABLENESS, -1),
		new Q("I formulate ideas clearly.", Q.OPENNESS, 1),
		new Q("I get deeply immersed in music.", Q.OPENNESS, 1),
		new Q("I don't mind messy people.", Q.CONSCIENTIOUSNESS, -1),
		new Q("I am always prepared.", Q.CONSCIENTIOUSNESS, 1),
		new Q("I tend to procrastinate a lot.", Q.CONSCIENTIOUSNESS, -1),
		new Q("I am easily discouraged.", Q.NEUROTICISM, 1),
		new Q("I don't daydream much.", Q.OPENNESS, -1),
		new Q("I am easily distracted.", Q.CONSCIENTIOUSNESS, -1),
		new Q("I am filled with doubts about things.", Q.NEUROTICISM, 1),
		new Q("I hold back my opinions.", Q.EXTRAVERSION, -1),		
		new Q("I laugh a lot.", Q.EXTRAVERSION, 1),
		new Q("I pay attention to details.", Q.CONSCIENTIOUSNESS, 1),
		new Q("I often engage in arguments.", Q.AGREEABLENESS, -1),
		new Q("I don't enjoy poetry.", Q.OPENNESS, -1),
		new Q("I finish what I start.", Q.CONSCIENTIOUSNESS, 1),		
		new Q("I get stressed easily.", Q.NEUROTICISM, 1),
		new Q("I liven up crowds.", Q.EXTRAVERSION, 1),
		new Q("I make people feel at ease.", Q.AGREEABLENESS, 1),
		new Q("I sympathize with other's feelings.", Q.AGREEABLENESS, 1),
		new Q("I lack the talent for influencing people.", Q.EXTRAVERSION, -1),
		new Q("I have a rich vocabulary.", Q.OPENNESS, 1),
		new Q("I hate to seem pushy.", Q.AGREEABLENESS, 1),
		new Q("I avoid drawing attention to myself.", Q.EXTRAVERSION, -1),
		new Q("I don't have time for other people.", Q.AGREEABLENESS, -1),
		new Q("I don't have a soft side.", Q.AGREEABLENESS, -1),
		new Q("I often initiate conversations.", Q.EXTRAVERSION, 1),
		new Q("I rarely lose my composure.", Q.NEUROTICISM, -1),
		new Q("I feel threatened easily.", Q.NEUROTICISM, 1)
	};
	
	public static class Q {
		public static final int EXTRAVERSION = 1;
		public static final int AGREEABLENESS = 2;
		public static final int CONSCIENTIOUSNESS = 3;
		public static final int NEUROTICISM = 4;
		public static final int OPENNESS = 5;
		
		public String question;
		public int type;
		public int amount;
		
		public Q(String question, int type, int amount) {
			this.question = question;
			this.type = type;
			this.amount = amount;
		}
	}
	
	public class SurveyResponse {
		public int[] personality = new int[data.length];
		public int age = -1;
		public String ethnicity = "";
		public int mcMonths = -1;
		public int mcYears = -1;
		public boolean isMale = false;
		public int progress = 0;
		
		public void writeToNBT(NBTTagCompound ntc) {
			ntc.setInteger("surveyPage", this.progress);
			ntc.setIntArray("surveyResponses", this.personality);
			ntc.setInteger("age", this.age);
			ntc.setString("ethnicity", this.ethnicity);
			ntc.setBoolean("gender", this.isMale);
			ntc.setInteger("mcMonths", this.mcMonths);
			ntc.setInteger("mcYears", this.mcYears);
		}
		
		public void readFromNBT(NBTTagCompound ntc) {
			this.personality = ntc.getIntArray("surveyResponses");
			this.age = ntc.getInteger("age");
			this.ethnicity = ntc.getString("ethnicity");
			this.isMale = ntc.getBoolean("gender");
			this.mcMonths = ntc.getInteger("mcMonths");
			this.mcYears = ntc.getInteger("mcYears");
			this.progress = ntc.getInteger("surveyPage");
		}
	}
	
	public static int getSurveyLength() {
		return data.length+4;
	}
	
	/**
	 * @param type Personality factor to score
	 * @return Value between -1.0 and +1.0
	 */
	public static float scoreResponses(int type, int[] responses) {
		int SAGREE = 0;
		int AGREE = 1;
		int DISAGREE = 3;
		int SDISAGREE = 4;
		
		float numOfType = 0;
		float score = 0;
		
		for (int i=0; i<data.length; i++) {
			if (data[i].type != type)
				continue;
			else {
				numOfType++;
				if (responses[i] == SAGREE)
					score += 2*data[i].amount;
				else if (responses[i] == AGREE)
					score += 1*data[i].amount;
				else if (responses[i] == SDISAGREE)
					score += -2*data[i].amount;
				else if (responses[i] == DISAGREE)
					score += -1*data[i].amount;
			}
		}
		
		return score/(2*numOfType);
	}
}
