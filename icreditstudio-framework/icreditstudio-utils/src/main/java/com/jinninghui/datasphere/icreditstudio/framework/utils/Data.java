package com.jinninghui.datasphere.icreditstudio.framework.utils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;


public class Data implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7590814996489280231L;
	private final Map<String, Object> values = Utils.newHashMap();

	public Data(Map<String, Object> values) {
		this.values.putAll(values);
	}

	public Data(Object...params  ) {
		if(params != null) {
			for(int i=0; i < params.length / 2; i++) {
				if(2 * i  + 1 >= params.length) {
					break;
				}
				String k = (String)params[2 * i];
				Object v = params[ 2* i + 1];
				this.values.put(k, v);
			}
		}
	}
	
	public Map<String, Object> getValues() {
		return values;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) values.get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, T def) {
		if(this.contains(key)) {
			return (T)values.get(key);
		} else {
			return def;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T remove(String key) {
		return (T)values.remove(key);
	}

	public <T> Data put(String key, T value) {
		values.put(key, value);
		return this;
	}

	public <T> Data putAll(Map<String, T> values) {
		if(values != null) {
			this.values.putAll(values);
		}
		return this;
	}

	public <T> Data putAll(Object...params  ) {
		if(params != null) {
			for(int i=0; i < params.length / 2; i++) {
				if(2 * i  + 1 >= params.length) {
					break;
				}
				String k = (String)params[2 * i];
				Object v = params[ 2* i + 1];
				this.values.put(k, v);
			}
		}
		return this;
	}

	public Data putAll(Data val) {
		if(val != null) {
			this.values.putAll(val.values);
		}
		return this;
	}

	public Set<String> keys() {
		return values.keySet();
	}

	public Collection<Object> values() {
		return values.values();
	}

	public boolean contains(String key) {
		return values.containsKey(key);
	}

	public boolean isEmpty() {
		return values.isEmpty();
	}

	public String getString(String key, String def) {
		String val = null;
		if(this.contains(key)) {
			val = getString(key);
		}
		val = StringUtils.isEmpty(val) ? def:val;
		return val;
	}
	
	public String getString(String key) {
		Object v = get(key);
		if (v == null) {
			return null;
		}
		if (v instanceof String) {
			return (String) v;
		} else if (v instanceof String[]) {
			return ((String[]) v)[0];
		} else {
			return v == null ? null : v.toString();
		}
	}

	@SuppressWarnings("rawtypes")
	public String[] getStrings(String key) {
		Object v = get(key);
		if(v == null) {
			return new String[]{};
		}
		if (v instanceof String) {
			return new String[]{(String)v};
		}
		if (v.getClass().isArray()) {
			String[] res = new String[Array.getLength(v)];
			for(int i=0; i< res.length; i++) {
				Object x = Array.get(v, i);
				//
				if(x == null) {
					res[i] = null;
				} else if(x instanceof String) {
					res[i] = (String)x;
				} else {
					res[i] = x.toString();
				}
			}
			return res;
		}
		if (v instanceof Collection) {
			Collection c = (Collection) v;
			String[] vs = new String[c.size()];
			int i = 0;
			for (Iterator it = c.iterator(); it.hasNext();) {
				Object iv = it.next();
				vs[i] = iv == null ? null : iv.toString();
				i++;
			}
			return vs;
		}
		return new String[]{v.toString()};
	}

	public boolean getBoolean(String key, Boolean def) {
		Object v = get(key);
		if (v instanceof Boolean) {
			return (Boolean) v;
		} else {
			String s = getString(key);
			return StringUtils.isEmpty(s) ? def : "true".equalsIgnoreCase(s);
		}
	}

	public boolean[] getBooleans(String key) {
		Object v = get(key);
		if (v instanceof boolean[]) {
			return (boolean[]) v;
		} else {
			String[] ss = getStrings(key);
			boolean[] res = new boolean[ss.length];
			for(int i=0; i< ss.length; i++) {
				res[i] = "true".equalsIgnoreCase(ss[i]);
			}
			return res;
		}
	}
	
	public int getInt(String key, int def) {
		Object v = get(key);
		if (v instanceof Integer) {
			return (Integer) v;
		} else {
			String s = getString(key);
			return StringUtils.isEmpty(s) ? def : Integer.parseInt(s.toString());
		}
	}

	public int[] getInts(String key, int def) {
		Object v = get(key);
		if(v == null) {
			return null;
		}
		
		if (v.getClass().isArray()) {
			int[] res = new int[Array.getLength(v)];
			for(int i=0; i< res.length; i++) {
				Object x = Array.get(v, i);
				if(x == null) {
					res[i] = def;
					continue;
				}
				//
				if(x instanceof Integer) {
					res[i] = (int)x;
				} else {
					String xs = x.toString();
					if(StringUtils.isEmpty(xs)) {
						res[i] = def;
					} else {
						res[i] = Integer.parseInt(xs);
					}
				}
			}
			return res;
		} else {
			if(v instanceof Integer) {
				return new int[]{(int)v};
			} else if(StringUtils.isEmpty(v.toString())) {
				return new int[]{def};
			} else {
				return new int[]{Integer.parseInt(v.toString())};
			}
		}
	}

	public long getLong(String key, long def) {
		Object v = get(key);
		if (v instanceof Long) {
			return (Long) v;
		} else {
			String s = getString(key);
			if(!StringUtils.isEmpty(s)) {
				return StringUtils.isEmpty(s) ? def : Long.parseLong(s.toString());
			} else {
				return def;
			}
		}
	}

	public long[] getLongs(String key, long def) {
		Object v = get(key);
		if(v == null) {
			return null;
		}
		
		if (v.getClass().isArray()) {
			long[] res = new long[Array.getLength(v)];
			for(int i=0; i< res.length; i++) {
				Object x = Array.get(v, i);
				if(x == null) {
					res[i] = def;
					continue;
				}
				//
				if(x instanceof Long) {
					res[i] = (long)x;
				} else {
					String xs = x.toString();
					if(StringUtils.isEmpty(xs)) {
						res[i] = def;
					} else {
						res[i] = Long.parseLong(xs);
					}
				}
			}
			return res;
		} else {
			if(v instanceof Integer) {
				return new long[]{(long)v};
			}
			else if(StringUtils.isEmpty(v.toString())) {
				return new long[]{def};
			} else {
				return new long[]{Long.parseLong(v.toString())};
			}
		}
	}

	public double getDouble(String key, double def) {
		Object v = get(key);
		if (v instanceof Double) {
			return (Double) v;
		} else {
			String s = getString(key);
			return StringUtils.isEmpty(s) ? def : Double.parseDouble(s.toString());
		}
	}

	public double[] getDoubles(String key, double def) {
		Object v = get(key);
		if(v == null) {
			return null;
		}
		
		if (v.getClass().isArray()) {
			double[] res = new double[Array.getLength(v)];
			for(int i=0; i< res.length; i++) {
				Object x = Array.get(v, i);
				if(x == null) {
					res[i] = def;
					continue;
				}
				//
				if(x instanceof Double) {
					res[i] = (double)x;
				} else {
					String xs = x.toString();
					if(StringUtils.isEmpty(xs)) {
						res[i] = def;
					} else {
						res[i] = Double.parseDouble(xs);
					}
				}
			}
			return res;
		} else {
			if(v instanceof Double) {
				return new double[]{(double)v};
			} else if(StringUtils.isEmpty(v.toString())) {
				return new double[]{def};
			} else {
				return new double[]{Double.parseDouble(v.toString())};
			}
		}
	}

	public float getFloat(String key, float def) {
		Object v = get(key);
		if (v instanceof Float) {
			return (Float) v;
		} else {
			String s = getString(key);
			return StringUtils.isEmpty(s) ? def : Float.parseFloat(s.toString());
		}
	}

	public float[] getFloats(String key, float def) {
		Object v = get(key);
		if(v == null) {
			return null;
		}
		
		if (v.getClass().isArray()) {
			float[] res = new float[Array.getLength(v)];
			for(int i=0; i< res.length; i++) {
				Object x = Array.get(v, i);
				if(x == null) {
					res[i] = def;
					continue;
				}
				//
				if(x instanceof Float) {
					res[i] = (float)x;
				} else {
					String xs = x.toString();
					if(StringUtils.isEmpty(xs)) {
						res[i] = def;
					} else {
						res[i] = Float.parseFloat(xs);
					}
				}
			}
			return res;
		} else {
			if(v instanceof Float) {
				return new float[]{(float)v};
			} else if(StringUtils.isEmpty(v.toString())) {
				return new float[]{def};
			} else {
				return new float[]{Float.parseFloat(v.toString())};
			}
		}
	}

	public Date getDate(String key) {
		Object v = get(key);
		if (v instanceof Date) {
			return (Date) v;
		} else {
			String s = getString(key);
			return StringUtils.isEmpty(s) ? null : DateUtils.parseDate(s);
		}
	}

	public Date[] getDates(String key) {
		Object v = get(key);
		if (v instanceof Date[]) {
			return (Date[]) v;
		} else {
			String[] ss = getStrings(key);
			Date[] res = new Date[ss.length];
			for(int i=0; i< ss.length; i++) {
				res[i] = StringUtils.isEmpty(ss[i]) ? null : DateUtils.parseDate(ss[i]);
			}
			return res;
		}
	}
	
	public List<Map<String, Object>> getRows(String key) {
		List<Map<String, Object>> list = this.get(key);
		return list;
	}

	@SuppressWarnings("unchecked")
	public Data getValue(String key) {
		Object v = get(key);
		if (v == null) {
			return null;
		}
		if (v instanceof Data) {
			return (Data) v;
		}
		if (v instanceof Map) {
			return (new Data((Map<String, Object>) v));
		}
		return null;
	}


	public Data merge(Data that, boolean overrides) {
		return this.merge(that, null, overrides);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Data merge(Data that, String[] fields, boolean overrides) {
		if(that == null) {
			return this;
		}
		
		Set<String> fieldset = Utils.newHashSet();
		if(fields != null) {
			fieldset.addAll(CollectionUtils.arrayToList(fields));
		}
		
		for (String key : that.keys()) {
			if(!fieldset.isEmpty() && !fieldset.contains(key)) {
				continue;
			}
			Object thisValue = this.get(key);
			Object thatValue = that.get(key);
			if (this.contains(key)) {
				if ((thisValue instanceof Map || thisValue instanceof Data) &&
						(thatValue instanceof Map || thatValue instanceof Data)) {
					Data thisData = thisValue instanceof Data ? (Data) thisValue : new Data((Map<String, Object>) thisValue);
					Data thatData = thatValue instanceof Data ? (Data) thatValue : new Data((Map<String, Object>) thatValue);
					thisData.merge(thatData, overrides);
					if(thisValue instanceof Map) {
						this.put(key, thisData.getValues());
					} 
				} else if(thisValue instanceof Collection && thatValue instanceof Collection){
					((Collection)thisValue).addAll((Collection)thatValue);
				}else {
					if (overrides) {
						this.put(key, thatValue);
					}
				}
			} else {
				this.put(key, thatValue);
			}
		}
		return this;
	}

	@Override
	public String toString() {
		return StringUtils.toString(this.values);
	}
}
