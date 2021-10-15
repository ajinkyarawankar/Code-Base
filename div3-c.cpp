#include <bits/stdc++.h> 
using namespace std; 
  
int main() 
{ 
    // added the two lines below 
    ios_base::sync_with_stdio(false); 
    cin.tie(NULL); 
    long long t,n;
    unordered_set<long long> cubes;
    for(long long i=1;i<=100000;i++){
        cubes.insert(i*i*i);
    }
    cin>>t;
    while(t--){
        cin>>n;
        bool ans = false;
        for(long long i=1;i<=100000;i++){
            if((n-(i*i*i))<=0) break;
            if(cubes.find(n-(i*i*i))!=cubes.end()){
                ans = true;
                break;
            }
        }
        if(ans) cout<<"YES\n";
        else cout<<"NO\n";
    }
}