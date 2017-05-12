package cache_imple;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class Main {
	private Student cache;
	private Scanner sc;

	public static void main(String[] args) {
		new Main().start();
	}

	private void start() {
		// load the Student
		cache = new Student();
		boolean dirtyRead = false;
		sc = new Scanner(System.in);
		System.out.println("loading the cache...................");
		ArrayList<Student> str = cache.get();
		System.out.println("++++++++++++++++++++++++++++++++");
		if (str == null) {
			System.out.println("file is empty");
		} else {
			for (Student s : str) {
				//override toString
				if (s != null)
					System.out.println(s);
			}
		}
		System.out.println("++++++++++++++++++++++++++++++++");
		System.out.println("cache loaded");
		System.out.println("Press the key for respective operations");
		boolean flag = false;
		while (true) {
			System.out.println("For add press 1.\nFor delete press 2.\nFor update press 3.\nFor read By id press 4."
					+ "\nFor shutDown press 5.\nFor list down the cache press 6.\nFor exit press 0");
			
			int ops = -1;
			try{
				ops= Integer.parseInt(sc.next());
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("not a number");
			} 
			
			switch (ops) {
			case 0:
				flag = true;
				break;
			case 1:
				add();
				dirtyRead = true;
				break;
			case 2:
				delete();
				break;
			case 3:
				update();
				break;
			case 4:
				read();
				break;
			case 5:
				dirtyRead = false;
				shutDown();
				break;
			case 6:
				if(dirtyRead){
					System.out.println("Please write the newly added data to file first");
				} else {
					listCache();
				}
				
				break;
			default:
				System.out.println("please enter valid input");
			}
			if (flag) {
				break;
			}
		}
	}
	
	private void listCache() {
		ArrayList<Student> arr= cache.get();
		for(Student s:arr){
			//override toString
			System.out.println(s);
		}
	}

	private void shutDown() {
		// TODO Auto-generated method stub
		cache.shutDown();
	}

	private void read() {
		System.out.println("please enter the object id you want to");
		Student s1 = cache.getById(sc.next());
		if (s1 == null) {
			System.out.println("id does not matched");
			return;
		}
		//override toString
		System.out.println(s1);

	}

	private void update() {
		Student s1 = new Student();
		System.out.println("please enter the id for obj that you want to update");
		s1.id = sc.next();
		System.out.println("please enter the name of student");
		String name = sc.next();
		int m;
		while (true) {
			System.out.println("please enter the marks of student out of 100");
			m = Integer.parseInt(sc.next());
			if (m > 100) {
				System.out.println("please enter the valid marks of student out of 100");
				continue;
			} else {
				break;
			}
		}
		s1.setmarks(m);
		s1.setName(name);
		String result = cache.update(s1);
		if (result == null) {
			System.out.println("Obj with id: " + s1.id + " is not found in the cache");
			return;
		}
		System.out.println("updated obj is  :" + result);
	}

	private void delete() {
		System.out.println("please enter the id of obj that you want to delete");
		String id = sc.next();
		if (!cache.delete(id)) {
			System.out.println("obj with id :" + id + " is not found in the cache");
			return;
		}
		System.out.println("delete obj with id is :" + id);
	}

	private void add() {
		System.out.println("please enter the name of student");
		String name = sc.next();
		int m;
		while (true) {
			System.out.println("please enter the marks of student out of 100");
			m = Integer.parseInt(sc.next());
			if (m > 100) {
				System.out.println("please enter the valid marks of student out of 100");
				continue;
			} else {
				break;
			}
		}
		Student s1 = new Student();
		s1.setmarks(m);
		s1.setName(name);
		s1 = cache.add(s1);
		System.out.println("added obj whose id is: "+ s1.id);
	}
}

class Student implements Serializable{
	public String id;
	private String name;
	private int marks;
	private int frequency = 0;
	private boolean update = false;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private ArrayList<Student> cache = new ArrayList<Student>();
	private static final String FILENAME = "C:\\test\\cache.ser";
	private FileInputStream fin = null;
	private FileOutputStream fout = null;
	private static int count  = 0;
	File f = new File(FILENAME);
	public String toString(){
		return  this.id+" "+this.name+" "+this.marks;
	}
	public void setName(String name) {
		this.name = name;
	}

	public boolean setmarks(int marks) {
		if (marks > 100) {
			return false;
		}
		this.marks = marks;
		return true;
	}

	public String getId(){
		return this.id;
	}
	public String getName(){
		return this.name;
	}
	public int getMarks(){
		return this.marks;
	}
	public Student add(Student obj) {
		obj.id = UUID.randomUUID().toString();
		obj.frequency = 0;
		if (cache.size() == 20 && deleteObjWithLowFreq()) {
		}
		cache.add(obj);
		return obj;
	}

	private boolean deleteObjWithLowFreq() {
		int min = Integer.MAX_VALUE;
		String id=null;
		Student temp = null;
		for(Student s1:cache){
			int frq = s1.frequency;
			if( frq < min)
			{
				 min = frq;
				 temp = s1;
			}  
		}
		cache.remove(temp);
		return true;
	}

	
	public ArrayList<Student> get() {
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			
			fin = new FileInputStream(FILENAME);
			ois = new ObjectInputStream(fin);
			Student s1;
			boolean flag = false;
			while(fin.available() > 0 && count<20){
				s1 = (Student)ois.readObject();
				for(Student s:cache){
					if(s.name.equals(s1.name)){
						flag = true;
						break;
					}
				}
				if(!flag){
					cache.add(s1);
					count++;	
				}
				
			}
			cache.sort(new Comparator<Student>() {
				@Override
				public int compare(Student arg0, Student arg1) {
					return arg0.marks-arg1.marks;
				}
			});
			return cache;
		}catch(EOFException ex) {
			System.out.println("nothing to read from file");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return cache; 
	}

	public Student getById(String id) {
		for(Student s1:cache){
			if(s1.id.equals(id)){
				s1.frequency++;
				return s1;
			}
		} 
		return null;
	}

	public boolean delete(String id) {
		for(Student s:cache){
			if(s.id.equals(id)){
				count--;
				cache.remove(s);
				return true;
			}
		}
		return false;
	}

	public String update(Student s1) {
		int index = cache.indexOf(s1);
		if (index>=0) {
			s1.frequency++;
			s1.update = true;
			cache.set(index, s1);
			String result = "{ Student:{ id:" + s1.id + ",name:" + s1.name + ",marks:" + s1.marks + "}}";
			return result;
		}
		return null;
	}

	public void shutDown() {
		try {
			fout = new FileOutputStream(FILENAME);
			oos = new ObjectOutputStream(fout);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Student st:cache) {
		    if(st.frequency == 0) {
		    	try{
		    			oos.writeObject(st);
						oos.flush();
				} catch (Exception ex) {
					ex.printStackTrace();
				} 
		    }else {
				//update file
			}
		} 
		System.out.println("cache cleared");
		cache.clear();
	} 
}
