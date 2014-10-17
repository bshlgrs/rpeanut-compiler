import DataTypes
import qualified Data.Map.Strict as M

type TypeMap = (M.Map Name Type)

makeError :: Show a => a -> Type -> Type -> b
makeError x e g = error ("Type error in " ++ show x
                            ++ ": expected " ++ show e ++ ", got " ++ show g)

-- takes objects, expected types, actual types
checkAllArgs :: [Expr] -> [Type] -> [Type] -> Bool
checkAllArgs [] [] [] = True
checkAllArgs (x:xs) (y:ys) (z:zs)
    | y == z = checkAllArgs xs ys zs
    | otherwise = makeError x y z

typecheckExpr :: TypeMap -> Expr -> Type
typecheckExpr m expr = case expr of
      (BinOp _ lhs rhs) -> case (typecheckExpr m lhs, typecheckExpr m rhs) of
          (ByteType, ByteType) -> ByteType
          (l, ByteType) -> makeError lhs ByteType l
          (_, r) -> makeError rhs ByteType r
      (LogicOp _ lhs rhs) -> typecheckExpr m (BinOp undefined lhs rhs)
      (Variable n) -> (m M.! n)
      (FunctionCall lhs args)
          | not $ checkAllArgs args argTypes (map (typecheckExpr m) args)
                        -> undefined
          | otherwise -> returnType
            where
              (FunctionType returnType argTypes) = typecheckExpr m lhs
      (LitInt _) -> ByteType
      (LitString _) -> StarType ByteType
      (AmpExpr name) -> StarType (m M.! name)
      (LoadExpr expr2) -> case typecheckExpr m expr2 of
            (StarType t) -> t
            t -> error ("Type error: " ++ show t ++ " should be pointer type")
      (StmtExpr lname rhs)
          | m M.! lname /= typecheckExpr m rhs -> makeError expr (m M.! lname)
                                                          (typecheckExpr m rhs)
          | otherwise -> m M.! lname
      (CastExpr t _) -> t -- TODO: make this more reliable?
      (StructAccessExpr expr name) -> undefined -- TODO: this