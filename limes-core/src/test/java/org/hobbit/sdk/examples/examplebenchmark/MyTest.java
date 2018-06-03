package org.hobbit.sdk.examples.examplebenchmark;

import com.mchange.v2.ser.SerializableUtils;
import java.io.Serializable;
import java.util.HashMap;
import org.hobbit.sdk.examples.examplebenchmark.benchmark.BenchmarkController;
import org.hobbit.sdk.examples.examplebenchmark.benchmark.DataGenerator;
import org.junit.Assert;
import org.junit.Test;

class Bar implements Serializable {
	long l = 0;
}

class Foo implements Serializable {
	int i = 32;
	HashMap<Integer, String> h = new HashMap<>();
	Bar bar =  new Bar();
}

public class MyTest {
	
	@Test
	public void testFoo() throws Exception {
		Foo foo = new Foo();
		foo.i = 2;
		foo.h.put(4, "haha");
		foo.bar.l = 3;
		byte[] bytes = SerializableUtils.toByteArray(foo);
		System.out.println(new String(bytes));
		Foo bar = (Foo) SerializableUtils.fromByteArray(bytes);
		System.out.println(bar.i);
		System.out.println(bar.bar.l);
		bar.h.entrySet().forEach(System.out::println);
	}
	
}
