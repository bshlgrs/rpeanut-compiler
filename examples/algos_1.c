int stringLength(string) () {
  out = 0;
  while (*string != 0) {
    string++;
    out++;
  }
  return out;
}

int isPalindrome(str, i, j) () {
  while (i < j) {
    if (str[i] != str[j]) {
      return 0;
    } else {
      i++;
      j--;
    }
  }
  return 1;
}

int longest_palindromic_substring(str)
    () {
  best = 0;
  len = stringLength(str);

  for (i = 0; (i < len); i++) {
    for (j = i+1; (j < len); j++) {
      k = j;

      if ((j-i) > best) {
        if (isPalindrome(str,i,j) == 1) {
          best = j - i;
        }
      }
    }
  }

  return best + 1;
}


int longest_common_substring(str1, str2, table)
                      () {
  len1 = str1.stringLength();
  len2 = str2.stringLength();
  biggest = 0;

  for (i = 0; (i < len1); i++) {
    for (j = 0; (j < len2); j++) {
      best = 0;
      if (str1[i] == str2[j]) {
        if (i * j > 0) {
          best = table[len2*(i-1) +(j - 1)] + 1;
        } else {
          best = 1;
        }
      } else {
        best = 0;
      }

      if ((i > 0) && (table[len2*(i-1) + j] > best)) {
        best = table[len2*(i-1) + j];
      }
      if ((j > 0) && (table[(len2*(i) + j) - 1] > best)) {
        best = table[len2*(i) + (j - 1)];
      }

      table[len2*(i) + j] = best;

      if (best > biggest) {
        biggest = best;
      }
    }
  }
  return biggest;
}

void main() ((table 20)) {
  putsInt(longest_common_substring("abcd","efbce",table));

  putsInt(longest_palindromic_substring("abcdca"));
}