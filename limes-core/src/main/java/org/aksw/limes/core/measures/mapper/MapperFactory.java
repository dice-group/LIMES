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
package org.aksw.limes.core.measures.mapper;

import org.aksw.limes.core.exceptions.InvalidMeasureException;
import org.aksw.limes.core.measures.mapper.phonetic.KoelnPhoneticMapper;
import org.aksw.limes.core.measures.mapper.phonetic.*;
import org.aksw.limes.core.measures.mapper.pointsets.OrchidMapper;
import org.aksw.limes.core.measures.mapper.pointsets.SymmetricHausdorffMapper;
import org.aksw.limes.core.measures.mapper.resourcesets.SetJaccardMapper;
import org.aksw.limes.core.measures.mapper.semantic.edgecounting.EdgeCountingSemanticMapper;
import org.aksw.limes.core.measures.mapper.space.HR3Mapper;
import org.aksw.limes.core.measures.mapper.string.*;
import org.aksw.limes.core.measures.mapper.string.fastngram.FastNGramMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex.EqualsMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex.OverlapsMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex.*;
import org.aksw.limes.core.measures.mapper.temporal.simpleTemporal.ConcurrentMapper;
import org.aksw.limes.core.measures.mapper.temporal.simpleTemporal.PredecessorMapper;
import org.aksw.limes.core.measures.mapper.temporal.simpleTemporal.SuccessorMapper;
import org.aksw.limes.core.measures.mapper.topology.*;
import org.aksw.limes.core.measures.measure.MeasureType;

/**
 * Implements the mapper factory class. For each measure name, the factory
 * returns an object of the corresponding mapper.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 *
 * @version 1.0
 */
public class MapperFactory {

    /**
     * Factory function for retrieving the desired mapper instance given an
     * input measure name.
     *
     * @param type,
     *            type of the measure
     *
     * @return a specific mapper instance
     */

    public static AMapper createMapper(MeasureType type) {
        switch (type) {
            case JAROWINKLER:
                return new JaroWinklerMapper();
            case JARO:
                return new JaroMapper();
            case QGRAMS:
                // @todo this is a hotfix, remove it in the future
            case TRIGRAM:
                return new FastNGramMapper();
            case COSINE:
            case OVERLAP:
            case JACCARD:
                return new PPJoinPlusPlus();
            case LEVENSHTEIN:
                return new EDJoinMapper();
            case EXACTMATCH:
                return new ExactMatchMapper();
            case SOUNDEX:
                return new SoundexMapper();
            case REFINEDSOUNDEX:
                return new RefinedSoundexMapper();
            case DOUBLEMETA:
                return new DoubleMetaPhoneMapper();
            case META:
                return new MetaPhoneMapper();
            case CAVERPHONE1:
                return new Caverphone1Mapper();
            case CAVERPHONE2:
                return new Caverphone2Mapper();
            case KOELN:
                return new KoelnPhoneticMapper();
            case DAITCHMOKOTOFF:
                return new DaitchMokotoffMapper();
            case MATCHRATING:
                return new MatchRatingMapper();
            case NYSIIS:
                return new NysiisMapper();
            case MONGEELKAN:
                return new MongeElkanMapper();
            case RATCLIFF:
                return new RatcliffObershelpMapper();
            ///////////////////////
            case EUCLIDEAN:
                return new HR3Mapper();
            case MANHATTAN:
                return new HR3Mapper();
            case GEO_ORTHODROMIC:
            case GEO_GREAT_ELLIPTIC:
                return new HR3Mapper();
            ///////////////////////
            case GEO_CENTROID_INDEXED_HAUSDORFF:
            case GEO_FAST_HAUSDORFF:
            case GEO_HAUSDORFF:
            case GEO_INDEXED_HAUSDORFF:
            case GEO_NAIVE_HAUSDORFF:
            case GEO_SCAN_INDEXED_HAUSDORFF:
                return new OrchidMapper();

            case GEO_SYMMETRIC_HAUSDORFF:
                return new SymmetricHausdorffMapper();
            ///////////////////////
            case GEO_MAX:
            case GEO_MEAN:
            case GEO_MIN:
            case GEO_AVG:
            case GEO_FRECHET:
            case GEO_LINK:
            case GEO_SUM_OF_MIN:
            case GEO_NAIVE_SURJECTION:
            case GEO_FAIR_SURJECTION:
                return new OrchidMapper();
            ///////////////////////
            case TMP_CONCURRENT:
                return new ConcurrentMapper();
            case TMP_PREDECESSOR:
                return new PredecessorMapper();
            case TMP_SUCCESSOR:
                return new SuccessorMapper();

            case TMP_AFTER:
                return new AfterMapper();
            case TMP_BEFORE:
                return new BeforeMapper();
            case TMP_DURING_REVERSE:
                return new DuringReverseMapper();
            case TMP_DURING:
                return new DuringMapper();
            case TMP_EQUALS:
                return new EqualsMapper();
            case TMP_FINISHES:
                return new FinishesMapper();
            case TMP_IS_FINISHED_BY:
                return new IsFinishedByMapper();
            case TMP_IS_MET_BY:
                return new IsMetByMapper();
            case TMP_IS_OVERLAPPED_BY:
                return new IsOverlappedByMapper();
            case TMP_IS_STARTED_BY:
                return new IsStartedByMapper();
            case TMP_MEETS:
                return new MeetsMapper();
            case TMP_OVERLAPS:
                return new OverlapsMapper();
            case TMP_STARTS:
                return new StartsMapper();

            ///////////////////////
            case TOP_CONTAINS:
                return new ContainsMapper();
            case TOP_COVERED_BY:
                return new CoveredbyMapper();
            case TOP_COVERS:
                return new CoversMapper();
            case TOP_CROSSES:
                return new CrossesMapper();
            case TOP_DISJOINT:
                return new DisjointMapper();
            case TOP_EQUALS:
                return new org.aksw.limes.core.measures.mapper.topology.EqualsMapper();
            case TOP_INTERSECTS:
                return new IntersectsMapper();
            case TOP_OVERLAPS:
                return new org.aksw.limes.core.measures.mapper.topology.OverlapsMapper();
            case TOP_TOUCHES:
                return new TouchesMapper();
            case TOP_WITHIN:
                return new WithinMapper();

            ///////////////////////
            case SET_JACCARD:
                return new SetJaccardMapper();
            ///////////////////////
            case SHORTEST_PATH:
                return new EdgeCountingSemanticMapper();
            case LCH:
                return new EdgeCountingSemanticMapper();
            case LI:
                return new EdgeCountingSemanticMapper();
            case WUPALMER:
                return new EdgeCountingSemanticMapper();
            default:
                throw new InvalidMeasureException(type.toString());
        }

    }

}
