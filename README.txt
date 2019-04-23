On stdlinux, run Parser as following steps:
1. make
2. java Parser < inputFile > outputFile
3. For large inputs, java -Xss20m Parser < inputFile > outputFile

There are some test cases:
(COND ((NULL T) (PLUS 1 2)) (T (MINUS 3 2)))
(CONS 2 (CONS 3(CONS 4 5)))
(MINUS (TIMES 9 (PLUS 1 2)) (PLUS 3 (MINUS 8 1)))
(GREATER (TIMES 1 (PLUS 2 2)) (PLUS 9 1))
(DEFUN F (X Y) (PLUS Y X))
(F (PLUS 5 6) (MINUS 6 5))
(DEFUN MEM (X LIST) (COND ((NULL LIST) NIL) ( (EQ X (CAR LIST)) T) (T (MEM X (CDR LIST)))))
(MEM 3 (QUOTE(2 3 4)))
(DEFUN FINDMIN (LIST) (COND ( (NULL (CDR LIST)) (CAR LIST) ) ( (NULL (CDR (CDR LIST))) (COND ( (GREATER (CAR LIST) (CAR (CDR LIST))) (CAR (CDR LIST)) ) ( T (CAR LIST))) ) ( (LESS (CAR LIST) (CAR (CDR LIST))) (FINDMIN (CONS (CAR LIST) (CDR (CDR LIST)))) ) ( T (FINDMIN (CDR LIST)) )))
(FINDMIN (QUOTE (100 10 5 6 7 8 1)))
(DEFUN FACTORIAL (N) (COND  ((EQ N 0) 1) (T (TIMES N (FACTORIAL (MINUS N 1))))))
(FACTORIAL 5)
