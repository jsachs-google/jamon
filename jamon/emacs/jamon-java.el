;;; jamon-java.el ---

; The contents of this file are subject to the Mozilla Public
; License Version 1.1 (the "License"); you may not use this file
; except in compliance with the License. You may obtain a copy of
; the License at http://www.mozilla.org/MPL/

; Software distributed under the License is distributed on an "AS
; IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
; implied. See the License for the specific language governing
; rights and limitations under the License.

; The Original Code is Jamon code, released February, 2003.

; The Initial Developer of the Original Code is Jay Sachs.  Portions
; created by Jay Sachs are Copyright (C) 2003 Jay Sachs.  All Rights
; Reserved.

; Contributor(s):

;;; Commentary:

;; This file contains the definition of utility functions for
;; navigating to Jamon source template files from the generated java
;; files.

;;; Code:

;;{{{

(defun jj-jump-to-source ()
  "From within a generated Jamon template implementation, jump to
  the corresponding Jamon template source file and line."
  (interactive)
  (if
      (catch 'not-jamon-impl
        (let ((template-file (jj-find-template-file-name))
              (position (jj-find-closest-position)))
          (find-file template-file)
          (beginning-of-buffer)
          (forward-line (- (car position) 1))
          (forward-char (- (cdr position) 1))
          ))
    (error "Not a generated Jamon implementation file")))

(defun jj-in-impl-p ()
  (save-excursion
    (beginning-of-buffer)
    (looking-at "^// Autogenerated Jamon implementation$")))

(defun jj-find-template-file-name ()
  ""
  (save-excursion
    (beginning-of-buffer)
    (if (not (looking-at "// Autogenerated Jamon implementation"))
        (throw 'not-jamon-impl 1)
      (forward-line 1)
      (if (not (looking-at "^// \\(.*\\)$"))
          (throw 'not-jamon-impl 2)
        (buffer-substring-no-properties (match-beginning 1) (match-end 1))
        )
      )
    )
  )

(defun jj-parse-int (m)
  (string-to-int
   (buffer-substring-no-properties (match-beginning m) (match-end m))))

(defconst jj-position-regexp "^ *// \\([0-9]+\\), \\([0-9]+\\)$")

(defun jj-closest-position (d)
  ""
  (beginning-of-line)
  (while (and
          (not (looking-at jj-position-regexp))
          (= (forward-line d) 0)))
  (if (looking-at jj-position-regexp)
      (cons (jj-parse-int 1) (jj-parse-int 2))))

(defun jj-find-closest-position ()
  ""
  (save-excursion
    (let ((p (point)))
      (or (jj-closest-position -1)
          (progn (goto-char p) (jj-closest-position 1))
          '(1 . 1)))))

(defvar jj-error-in-process nil)

(defun jj-previous-error ()
  (interactive)
  (cond (jj-error-in-process
         (switch-to-buffer (cddr jj-error-in-process))
         (goto-char (car jj-error-in-process))
         (setq jj-error-in-process nil)
         )
        (t (previous-error))))

(defun jj-next-error (&optional ARGP)
  (interactive "P")
  (cond ((and (not ARGP) (not jj-error-in-process) (jj-in-impl-p))
         (setq jj-error-in-process
               (cons (point) (cons (mark t) (current-buffer))))
         (jj-jump-to-source))
        (t
         (setq jj-error-in-process nil)
         (next-error ARGP))))

(provide 'jamon-java)

;;}}}