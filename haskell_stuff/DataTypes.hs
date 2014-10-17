module DataTypes
where

type Name = String

data BinaryOperator = AddOp | MulOp | SubOp | DivOp | AndOp | OrOp
  deriving (Show, Eq)

data LogicOperator = AndLogicOp | OrLogicOp
  deriving (Show, Eq)

data Type = ByteType
          | StarType Type
          | FunctionType Type [Type]
          | ProductType [(Name, Type)]
          | SumType [(Name, Type)]
          | NamedType Name
  deriving (Show, Eq)

-- I currently have no way of expressing exprs like the rhs of x = ((&b+1) = 2);
data Expr = BinOp BinaryOperator Expr Expr -- a+b
          | LogicOp LogicOperator Expr Expr -- a && b
          | Variable Name -- a
          | FunctionCall Expr [Expr] -- f(a,b,c)
          | LitInt Int  -- 4
          | LitString String -- "Hello"
          | AmpExpr Name -- &a. I need to check that the arg to this is always a string.
          | LoadExpr Expr -- *(a + 3)
          | StmtExpr Name Expr -- a = b
          | CastExpr Type Expr -- (int) b
          | StructAccessExpr Expr Name -- a.b
  deriving (Show, Eq)

data Stmt = Assign Name Expr
          | StructAssignExpr Expr String Expr
          | StarAssign Expr Expr
          | VoidFunctionCall Expr
          | IfStmt Expr [Stmt] [Stmt]
          | ForLoop (Maybe Stmt) (Maybe Expr) (Maybe Stmt) [Stmt]
          | WhileLoop Expr [Stmt]
          | Return Name Expr
  deriving (Show, Eq)

data VarDec = VarDec Type String
  deriving (Show, Eq)

data FunctionDeclaration = FunctionDeclaration Name Type [(Name, Type)]
                                    [VarDec] [Stmt]
  deriving (Show, Eq)

data TypeDeclaration = TypeDeclaration Name Type
  deriving (Show, Eq)

data Program = Program [TypeDeclaration] [VarDec] [FunctionDeclaration]
  deriving (Show, Eq)