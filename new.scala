abstract class Term
case class Var(name: String) extends Term
case class Sum(lhs: Term, rhs: Term) extends Term
case class Lit(f: Int) extends Term

