module CompilerTypes where

import qualified Data.Map as M

type Name = String

data Expression = Op String Expression Expression
                | Var Name
                | Const Int
                | Star Expression
                | Ampersand Name
                | FunctionCall Name [Expression]
                | FunctionPointerCall Expression [Expression]
          deriving (Show, Eq)

data Statement = NakedExpression Expression
               | Assignment Name Expression
               | PointerAssignment Expression Expression
               | If Expression [Statement]
               | IfElse Expression [Statement] [Statement]
               | While Expression [Statement]
               | Return Expression
          deriving (Show, Eq)

data Function = Function Name
                      [Name] -- arguments
                        [Name] -- local vars
                          [Statement] -- body
          deriving (Show, Eq)

data Register = Register Int
              | SP

data Address = Address Int

data Instruction = Add Register Register Register
                 | Sub Register Register Register
                 | Call Address
                 | ReturnInst
                 | Jump Address
                 | Jumpz Register Address
                 | Push Register
                 | ImmediateLoad Register
                 | BDLoad Register Int Register
                 | Halt

-- This ignores the possibility of other stuff than function definitions.
data Program = Program [Function]

