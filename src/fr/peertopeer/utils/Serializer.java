package fr.peertopeer.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class Serializer {
	public static byte[] serialize(Object obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutput bo = null;
        try {
			bo = new ObjectOutputStream(baos);
			bo.writeObject(obj);
			bo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return baos.toByteArray();
	}
	
	public static Object deserialize(byte[] datas) {
        ByteArrayInputStream baos = new ByteArrayInputStream(datas);
        ObjectInput oi = null;
        try {
			oi = new ObjectInputStream(baos);
			return oi.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
	}
}
