# $Id: labeltest.py 8000 2008-10-24 15:42:15Z aboudreault $
#
# Project:  MapServer
# Purpose:  xUnit style Python mapscript tests of Map
# Author:   Sean Gillies, sgillies@frii.com
#
# ===========================================================================
# Copyright (c) 2004, Sean Gillies
# 
# Permission is hereby granted, free of charge, to any person obtaining a
# copy of this software and associated documentation files (the "Software"),
# to deal in the Software without restriction, including without limitation
# the rights to use, copy, modify, merge, publish, distribute, sublicense,
# and/or sell copies of the Software, and to permit persons to whom the
# Software is furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included
# in all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
# OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
# THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
# FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
# DEALINGS IN THE SOFTWARE.
# ===========================================================================
#
# Execute this module as a script from mapserver/mapscript/python
#
#     python tests/cases/labeltest.py -v
#
# ===========================================================================

import os, sys
import unittest

# the testing module helps us import the pre-installed mapscript
from testing import mapscript, MapTestCase

# ===========================================================================
# Test begins now

class NewLabelsTestCase(MapTestCase):

    def testLabelBinding(self):
        """attribute binding can be set and get"""
        new_label = mapscript.labelObj()
        assert (not new_label.getBinding(mapscript.MS_LABEL_BINDING_COLOR))
        new_label.setBinding(mapscript.MS_LABEL_BINDING_COLOR,"NEW_BINDING")
        assert (new_label.getBinding(mapscript.MS_LABEL_BINDING_COLOR) == "NEW_BINDING")

class LabelCacheMemberTestCase(MapTestCase):

    def testCacheMemberText(self):
        """string attribute has been renamed to 'text' (bug 852)"""
        img = self.map.draw()
        assert self.map.labelcache.numlabels == 2, self.map.labelcache.numlabels
        label = self.map.nextLabel()
        assert label.text == 'A Point', label.text
        

# ===========================================================================
# Run the tests outside of the main suite

if __name__ == '__main__':
    unittest.main()
    
