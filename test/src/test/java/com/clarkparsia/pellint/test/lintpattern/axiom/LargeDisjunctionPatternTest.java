package com.clarkparsia.pellint.test.lintpattern.axiom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellint.lintpattern.axiom.LargeDisjunctionPattern;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.test.PellintTestCase;

/**
 * <p>
 * Title: 
 * </p>
 * <p>
 * Description: 
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Harris Lin
 */
public class LargeDisjunctionPatternTest extends PellintTestCase {
	
	private LargeDisjunctionPattern m_Pattern;
	
	@Before
	public void setUp() throws OWLOntologyCreationException {
		super.setUp();
		m_Pattern = new LargeDisjunctionPattern();
	}
	
	@Test
	public void testNone() throws OWLException {
		m_Pattern.setMaxAllowed(3);

		OWLClassExpression union = OWL.or(m_Cls[1], OWL.Thing, OWL.Nothing);
		OWLAxiom axiom = OWL.subClassOf(m_Cls[0], union);
		assertNull(m_Pattern.match(m_Ontology, axiom));
		assertFalse(m_Pattern.isFixable());
	}
	
	@Test
	public void testSimple() throws OWLException {
		m_Pattern.setMaxAllowed(2);

		OWLClassExpression union = OWL.or(m_Cls[1], m_Cls[2], m_Cls[3]);
		OWLAxiom axiom = OWL.subClassOf(m_Cls[0], union);
		Lint lint = m_Pattern.match(m_Ontology, axiom);
		assertNotNull(lint);
		assertSame(m_Pattern, lint.getPattern());
		assertEquals(1, lint.getParticipatingAxioms().size());
		assertNull(lint.getLintFixer());
		assertEquals(3.0, lint.getSeverity().doubleValue(), DOUBLE_DELTA);
		assertSame(m_Ontology, lint.getParticipatingOntology());
	}
	
	@Test
	public void testDisjointClasses() throws OWLException {
		m_Pattern.setMaxAllowed(2);
		OWLClassExpression union = OWL.or(m_Cls[1], m_Cls[2], m_Cls[3]);
		OWLAxiom axiom = OWL.disjointClasses(m_Cls[0], union);
		assertNotNull(m_Pattern.match(m_Ontology, axiom));
	}

	@Test
	public void testNested1() throws OWLException {
		m_Pattern.setMaxAllowed(2);

		OWLClassExpression union = OWL.or(m_Cls[1], m_Cls[2], m_Cls[3]);
		OWLClassExpression all = OWL.all(m_Pro[0], union);
		OWLClassExpression and = OWL.and(all, m_Cls[4]);
		OWLAxiom axiom = OWL.equivalentClasses(and, m_Cls[0]);
		assertNotNull(m_Pattern.match(m_Ontology, axiom));
	}
	
	@Test
	public void testNested2() throws OWLException {
		m_Pattern.setMaxAllowed(2);

		OWLClassExpression union1 = OWL.or(m_Cls[1], m_Cls[2], m_Cls[3]);
		OWLClassExpression all = OWL.all(m_Pro[0], union1);
		OWLClassExpression union2 = OWL.or(all, m_Cls[4]);
		OWLAxiom axiom = OWL.equivalentClasses(union2, m_Cls[0]);
		assertNotNull(m_Pattern.match(m_Ontology, axiom));
	}
}
