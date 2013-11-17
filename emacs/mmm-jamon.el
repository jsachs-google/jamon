;;; mmm-jamon.el --- MMM submode class for Jamon templates

; This Source Code Form is subject to the terms of the Mozilla Public
; License, v. 2.0. If a copy of the MPL was not distributed with this
; file, You can obtain one at http://mozilla.org/MPL/2.0/.

;;; Commentary:

;; This file contains the definition of an MMM Mode submode class for
;; editing Jamon templates.

;; To use this, put something like the following into your .emacs file:

;; (require 'mmm-jamon)
;; (add-to-list 'auto-mode-alist '("\\.jamon" . html-mode))
;; (mmm-add-mode-ext-class 'html-mode "\\.jamon" 'jamon)

;;; Code:

(require 'mmm-compat)
(require 'mmm-vars)
(require 'mmm-auto)

;;{{{ Java Tags

(defvar mmm-jamon-java-tags
  '("java" "class"))

(defvar mmm-jamon-java-tags-regexp
  (concat "<%" (mmm-regexp-opt mmm-jamon-java-tags t) ">")
  "Matches tags beginning Jamon sections containing Java code.
Saves the name of the tag matched.")

;;}}}
;;{{{ Add Classes

(mmm-add-group
 'jamon
 `((jamon-java
    :submode java
    :match-face (("<%java>" . mmm-code-submode-face)
                 ("<%class>" . mmm-class-submode-face))
    :front ,mmm-jamon-java-tags-regexp
    :back "</%~1>"
    :save-matches 1)
   (jamon-one-line
    :submode java
    :face mmm-code-submode-face
    :front "^%"
    :back "$")
   (jamon-inline
    :submode java
    :face mmm-output-submode-face
    :front "<% "
    :back "%>")))

;;}}}

(provide 'mmm-jamon)

;;; mmm-jamon.el ends here
