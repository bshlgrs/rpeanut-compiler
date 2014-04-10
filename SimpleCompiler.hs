module SimpleCompiler where

import qualified Data.Map as M
import Data.List
import Data.Maybe

import CompilerTypes

type Scope = M.Map Name Int
type RegisterAllocations = ([Maybe Name], Int)

maxRegisters = 10

blankAlloc = ([], -1)

posInRegisters :: RegisterAllocations -> Name -> Maybe Int
posInRegisters alloc name = case elemIndex (Just name) (fst alloc) of
              (Just x) -> Just $ (snd alloc - x) `mod` maxRegisters
              Nothing -> Nothing

saveNewVal :: RegisterAllocations -> Name -> RegisterAllocations
saveNewVal (registers,offset) name = (take maxRegisters (Just name:registers), offset + 1)

saveConstant :: RegisterAllocations -> RegisterAllocations
saveConstant (x, y) = (take maxRegisters (Nothing:x), y+1)

front :: RegisterAllocations -> Register
front (registers, offset) = Register $ offset - length registers


allocateIfNeeded :: Name -> RegisterAllocations -> Scope ->
                                ([Instruction], RegisterAllocations, Register)
allocateIfNeeded name alloc scope = case posInRegisters alloc name of
              (Just x) -> ([], alloc, Register x)
              Nothing -> ([BDLoad SP (fromJust $ M.lookup name scope) newPos],
                                      newAlloc, newPos)
                where
                    newAlloc = saveNewVal alloc name
                    newPos = Register . fromJust $ posInRegisters newAlloc name

compileExpression :: Expression -> Scope -> RegisterAllocations ->
                                  ([Instruction], RegisterAllocations, Register)
compileExpression expression scope alloc = case expression of
        (Var name) -> allocateIfNeeded name alloc scope
        (Const num) -> ([ImmediateLoad num numPosition],
                                    newAlloc,
                                      numPosition)
            where
                numPosition = front newAlloc
                newAlloc = saveConstant alloc
        (Op op l r) -> (lCode ++ rCode ++ [opCode], alloc4, front alloc4)
            where
                (lCode, alloc2, leftRegister) = compileExpression l scope alloc
                (rCode, alloc3, rightRegister) = compileExpression r scope alloc2
                alloc4 = saveConstant alloc3
                opCode = (opToInstruction op) leftRegister rightRegister
                                                                (front alloc4)

simpleScope :: Scope
simpleScope = (M.fromAscList [("x",0), ("y",1)])

opToInstruction :: String -> (Register -> Register -> Register -> Instruction)
opToInstruction "+" = Add