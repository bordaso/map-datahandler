package ec33nw.map.datahandler.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.google.protobuf.ByteString;

import iexcloud.gen.DecimalValue;
import pl.zankowski.iextrading4j.api.stocks.v1.Report;

public class GrpcRestDtoParser{
	
	public static final GrpcRestDtoParser INSTANCE = new GrpcRestDtoParser();
	
	private GrpcRestDtoParser() {
		super();
	}
	
	public <BLDR extends com.google.protobuf.GeneratedMessageV3.Builder<?>, RESTTYP extends Report>  
	BLDR parseRestToGrpc(Class<?> grpcClass, Class<?> restClass, BLDR builderObj, RESTTYP restObj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchMethodException{
		List<Field> restFields = getAllFields(new ArrayList<>(), restClass);		
		List<Field> grpcFields = Arrays.asList(grpcClass.getDeclaredFields());	
		for(Field restField : restFields) {
			for(Field grpcField : grpcFields) {
				if(setRestToGrpcField(restField, restObj, grpcField, builderObj)) {
					break;
				}
			}
		}
		
		return builderObj;
	}


	private <BLDR extends com.google.protobuf.GeneratedMessageV3.Builder<?>, RESTTYP extends Report>  
	Boolean setRestToGrpcField(Field restField, RESTTYP restObj, Field grpcField, BLDR grpcBuilder) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchMethodException {
		if(grpcField.getName().equalsIgnoreCase((restField.getName()+"_"))) {				
				try {
					restField.setAccessible(true);
					BigDecimal restValueBd = (BigDecimal)restField.get(restObj);
					Method setter = grpcBuilder.getClass().getDeclaredMethod("set"+(restField.getName().substring(0, 1).toUpperCase() + restField.getName().substring(1)), DecimalValue.class);
					DecimalValue serializedBd = DecimalValue.newBuilder()
					        .setScale(Objects.isNull(restValueBd)?1:restValueBd.scale())
					        .setPrecision(Objects.isNull(restValueBd)?1:restValueBd.precision())
					        .setValue(ByteString.copyFrom(Objects.isNull(restValueBd)?new byte[1]:restValueBd.unscaledValue().toByteArray()))
					        .build();					
					setter.invoke(grpcBuilder, serializedBd);
				} catch (ClassCastException e) {
					String restValueStr = (String)restField.get(restObj);
					Method setter = grpcBuilder.getClass().getDeclaredMethod("set"+(restField.getName().substring(0, 1).toUpperCase() + restField.getName().substring(1)), String.class);
					setter.invoke(grpcBuilder, restValueStr);
				} 
			return true;
		}
		return false;
	}	
	
	private List<Field> getAllFields(List<Field> fields, Class<?> type) {
		fields.addAll(Arrays.asList(type.getDeclaredFields()));
 
	    if (type.getSuperclass() != null) {
	        getAllFields(fields, type.getSuperclass());
	    }

	    return fields;
	}
}
