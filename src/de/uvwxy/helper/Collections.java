package de.uvwxy.helper;

import java.util.ArrayList;
import java.util.List;

public class Collections {
	public <E> List<E> getAsetminusB(List<E> setA, List<E> setB, IEquals<E> o) {
		ArrayList<E> ret = new ArrayList<E>();
		for (E e1 : setA) {
			boolean e1InSetB = false;
			for (E e2 : setB) {
				if (o.checkEquals(e1, e2)) {
					e1InSetB = true;
				}
			}

			if (!e1InSetB) {
				ret.add(e1);
			}
		}
		return ret;
	}
}
