/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.execution.planning.plan;


import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class InstructionTest {

    @Test
    public void equals() {
        System.out.println("Equal");


        Instruction i = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction i2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);

        assertTrue(i.equals(i2));

        //different measureExpression
        Instruction i3 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        assertTrue(!i.equals(i3));

        //different command
        Instruction i4 = new Instruction(Command.FILTER, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        assertTrue(!i.equals(i4));

        //different threshold
        Instruction i5 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.45", -1, -1, 0);
        assertTrue(!i.equals(i5));

        //different sourceMapping field
        Instruction i6 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", 12, -1, 0);
        assertTrue(i.equals(i6));

        //different mainThreshold
        Instruction i7 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        i7.setMainThreshold("lol");
        assertTrue(!i.equals(i7));

        ////////////////////////////////////////////////////////////////////////////////////////////////


    }

    @Test
    public void Clone() {
        System.out.println("clone");


        Instruction i = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        i.setMainThreshold("0.23245");
        Instruction i2 = i.clone();

        assertTrue(i.equals(i2));


        assertTrue(i.getCommand().hashCode() == i2.getCommand().hashCode());
        assertTrue(i.getMeasureExpression().hashCode() == i2.getMeasureExpression().hashCode());
        assertTrue(i.getThreshold().hashCode() == i2.getThreshold().hashCode());
        assertTrue(i.getMainThreshold().hashCode() == i2.getMainThreshold().hashCode());


    }

}
