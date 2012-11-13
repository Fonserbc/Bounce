package com.fonserbc.bounce;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import android.content.Context;
import android.util.Log;

public class RankingManager {
	
	private static RankingManager instance = null;
	
	public int RANKING_LENGTH = 10;
	
	public String FILE_NAME = "com.fonserbc.bounce.ranking";
	
	private Context context;
	
	public class rankingStruct {
		int points;
		String name;
		
		public rankingStruct() {
			points = -1;
			name = "---";
		}
	}
	
	public rankingStruct [][] Ranking;
	
	public static RankingManager getInstance(Context context) throws IOException {
		if (instance == null) {
			instance = new RankingManager(context);
		}
		
		return instance;
	}
	
	protected RankingManager (Context context) throws IOException {
		this.context = context;
		
		Ranking = new rankingStruct[3][RANKING_LENGTH];
		
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < RANKING_LENGTH; ++j) {
				Ranking[i][j] = new rankingStruct();
			}
		
		FileInputStream in;
		try {
			in = context.openFileInput(FILE_NAME);
		} catch (FileNotFoundException e) {
			initFile();
			
			in = context.openFileInput(FILE_NAME);
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
		
		for (int i = 0; i < 3; ++i) {
			String line = reader.readLine();
			String[] names = line.split(" ");
			//Log.v("BOUNCE", "names lenght de "+i+" is "+names.length);
			for (int j = 0; j < RANKING_LENGTH; ++j) {
				if (j >= names.length || names[j].equals("")) Ranking[i][j].name = "---";
				else Ranking[i][j].name = names[j];
			}
			
			line = reader.readLine();
			String[] points = line.split(" ");
			//Log.v("BOUNCE", "Points lenght de "+i+" is "+points.length);
			for (int j = 0; j < RANKING_LENGTH; ++j) {
				if (j >= points.length || names[j].equals("")) Ranking[i][j].points = -1;
				else Ranking[i][j].points = Integer.parseInt(points[j]);
			}
		}
		
		reader.close();
		in.close();
	}
	
	private void initFile() throws IOException {
		FileOutputStream out = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, Charset.forName("UTF-8")));
		
		for (int i = 0; i < 6; ++i) writer.newLine();
		
		writer.close();
		out.close();
	}

	public void saveRanking() {
		FileOutputStream out;
		try {
			out = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, Charset.forName("UTF-8")));
			
			for (int i = 0; i < 3; ++i) {
				for (int j = 0; j < RANKING_LENGTH; ++j) {
					if (Ranking[i][j].name == "---") break;
					else writer.write(Ranking[i][j].name);
				}
				writer.newLine();
				
				for (int j = 0; j < RANKING_LENGTH; ++j) {
					if (Ranking[i][j].points <= -1) break;
					else writer.write(((Integer)(Ranking[i][j].points)).toString());
				}
				writer.newLine();
			}
			
			writer.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
