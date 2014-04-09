module SimpleCompiler where

import qualified Data.Map as M
import Data.List

import CompilerTypes

type Scope = M.Map Name Int
type RegisterAllocations = ([Name], Int)

maxRegisters = 10

posInRegisters :: RegisterAllocations -> Name -> Maybe Int
posInRegisters alloc name = case elemIndex name (fst alloc) of
              (Just x) -> (snd alloc - x) `mod` maxRegisters
              Nothing -> Nothing

compileExpression :: Expression -> Scope -> RegisterAllocations ->
                                  ([Instruction], RegisterAllocations, Register)
compileExpression (Var name) scope allocations =
                (code, newAllocations, output)
          where
              (code, newAllocations, output) = allocateIfNeeded name allocations scope


saveNewVal :: RegisterAllocations -> Name -> RegisterAllocations
saveNewVal (registers offset) name = (take maxRegisters (name:registers), offset + 1)


allocateIfNeeded :: Name -> RegisterAllocations -> Scope ->
                                ([Instruction], RegisterAllocations, Register)
allocateIfNeeded name alloc scope = case posInRegisters alloc name of
              (Just x) -> ([], alloc, name)
              Nothing -> ([BDLoad SP (lookup name scope) (Register newPos)],
                                      newAlloc, newPos)
                where
                    newAlloc = saveNewVal alloc name
                    newPos = posInRegisters newAlloc name

